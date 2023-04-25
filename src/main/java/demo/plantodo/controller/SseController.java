package demo.plantodo.controller;
import demo.plantodo.VO.UrgentMsgInfoVO;
import demo.plantodo.converter.ObjToJsonConverter;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.logger.SseTrace;
import demo.plantodo.service.AuthService;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequestMapping("/sse")
@RestController
@RequiredArgsConstructor
public class SseController {
    private SseTrace trace = new SseTrace();

    private final AuthService authService;
    private final MemberService memberService;
    private final PlanService planService;

    private final RedisClient redisClient;

    private static final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    private void setLastSentTime(Long memberId, LocalDateTime time) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        commands.set(String.valueOf(memberId), time.toString());
        connection.close();
    }

    private LocalDateTime getLastSentTime(Long memberId) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        String s = commands.get(String.valueOf(memberId));
        connection.close();
        return LocalDateTime.parse(s);
    }

    private void updateLastSentTime(Long memberId, LocalDateTime time) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        commands.del(String.valueOf(memberId));
        commands.set(String.valueOf(memberId), time.toString());
    }

    @PostMapping("/last")
    public void setLastSentTime(@CookieValue(name = "AUTH") String key) {
        Long memberId = authService.getMemberIdByKey(key);
        setLastSentTime(memberId, LocalDateTime.now());
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@CookieValue(name = "AUTH") String key) throws IOException {
        Long memberId = authService.getMemberIdByKey(key);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clients.put(memberId, emitter);
        SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                .id(String.valueOf(memberId))
                .name("dummy")
                .data("EventStream Created. This is dummy data of [userId : " + memberId + "]");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        if (commands.exists(String.valueOf(memberId)) == 0) {
            setLastSentTime(memberId, LocalDateTime.MIN);
        }
        emitter.send(initEvent);
        emitter.onTimeout(() -> {
            clients.remove(memberId);
            log.info("Emitter Timeout");
        });
        emitter.onCompletion(() -> {
            clients.remove(memberId);
            log.info("Emitter Completion");
        });
        emitter.onError((ex) -> {
            if (ex instanceof TimeoutException) {
                log.info("IOException -> SSE Connection Timeout");
            } else if (ex instanceof PersistenceException) {
                log.info("IOException -> something wrong in DB(jpa)");
            }
        });
        return emitter;
    }

    @GetMapping("/sendAlarm")
    public void sendAlarm(@CookieValue(name = "AUTH") String key) {
        Long memberId = authService.getMemberIdByKey(key);

        int deadline_alarm_term = memberService.findOne(memberId).getSettings().getDeadline_alarm_term();
        /*실험용 (나중에 new ArrayList<>() 차이에 Plan 리스트를 조회해서 넣어야 함*/
        Thread loginThread = new Thread(new SendAlarmRunnable(planService, memberId, deadline_alarm_term));
        loginThread.setName("loginThread" + memberId + new Random().nextInt());
        loginThread.start();
    }

    @GetMapping("/quitAlarm")
    public void quitAlarm(@CookieValue(name = "AUTH") String key) {
        Long memberId = authService.getMemberIdByKey(key);

        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Thread thread : traces.keySet()) {
            if (thread.getName().equals("loginThread"+memberId)) {
                thread.interrupt();
                break;
            }
        }
    }

    class SendAlarmRunnable implements Runnable {

        private final PlanService planService;

        private Long memberId;
        private int alarm_term;
        private boolean flag;

        public SendAlarmRunnable(PlanService planService, Long memberId, int alarm_term) {
            this.planService = planService;
            this.memberId = memberId;
            this.alarm_term = alarm_term;
            this.flag = true;
        }

        @Override
        public void run() {
            int waitCnt = 0;
            while (!Thread.interrupted()) {
                List<PlanTerm> plans = planService.findUrgentPlans(memberId);
                if (plans.size() == 0) {
                    trace.simpleLog("No Urgent Plans - Waiting");
                    break;
                }
                if (!clients.containsKey(memberId)) {
                    waitCnt++;
                    trace.simpleLog("Cannot Find Client ID - Waiting");
                    if (waitCnt >= 5) break;
                    continue;
                }
                SseEmitter client = clients.get(memberId);
                try {
                    LocalDateTime msgLastSentTime = getLastSentTime(memberId);
                    LocalDateTime now = LocalDateTime.now();

                    if (msgLastSentTime.isEqual(LocalDateTime.MIN)) {
                        trace.simpleLog("First Message Transfer");
                    } else {
                        long duration = Duration.between(msgLastSentTime, now).toMillis();
                        long waitTime = duration % (alarm_term * 60000);
                        Thread.sleep(waitTime);
                        if (flag) {
                            trace.simpleLog("Adjusting...");
                            flag = false;
                        } else {
                            trace.intervalLog("Message Transfer", msgLastSentTime, alarm_term);
                        }
                    }
                    client.send(new ObjToJsonConverter().convert(new UrgentMsgInfoVO(plans.size(), plans.get(0).getId())));
                    updateLastSentTime(memberId, now);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    break;
                } catch (IllegalStateException ise) {
                    ise.printStackTrace();
                    log.error("ResponseBodyEmitter has already completed.. client have to deal with this");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    break;
                }
            }
        }
    }
}

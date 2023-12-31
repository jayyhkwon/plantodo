package demo.plantodo.controller;
import ch.qos.logback.core.net.server.Client;
import demo.plantodo.VO.ClientKeySet;
import demo.plantodo.VO.UrgentMsgInfoVO;
import demo.plantodo.converter.ObjToJsonConverter;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.logger.SseTrace;
import demo.plantodo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequestMapping("/sse")
@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseTrace trace = new SseTrace();

    private final AuthService authService;
    private final MemberService memberService;
    private final PlanService planService;

    private static final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    private final CacheService cacheService;

    private static volatile boolean able = true;

    @GetMapping(value = "/able")
    public Map<String, Boolean> getAble() {
        return Collections.singletonMap("able", able);
    }

    @GetMapping(value = "/subscribe")
    public SseEmitter subscribe(@CookieValue(name = "AUTH") String key,
                                HttpServletResponse response) throws IOException {
        Long memberId = authService.getMemberIdByKey(key);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clients.put(memberId, emitter);
        SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                .id(String.valueOf(memberId))
                .name("dummy")
                .data("EventStream Created");

        if (Boolean.FALSE.equals(cacheService.hasKey(String.valueOf(memberId)))) {
            cacheService.set(String.valueOf(memberId), LocalDateTime.MIN.toString());
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
            } else if (ex instanceof IOException) {
                log.info("IOException -> You have to wait until connection may be re-established by client");
                able = true;
            }
        });
        response.addHeader("X-Accel-Buffering", "no");
        return emitter;
    }

    @GetMapping(value = "/sendAlarm")
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

        public SendAlarmRunnable(PlanService planService, Long memberId, int alarm_term) {
            this.planService = planService;
            this.memberId = memberId;
            this.alarm_term = alarm_term;
        }

        @Override
        public void run() {
            able = false;
            int waitCnt = 0;
            while (!Thread.interrupted()) {
                // 현재 알림을 보낼 수 있는 plan이 있는지 확인
                List<PlanTerm> plans = planService.findUrgentPlans(memberId);
                if (plans.size() == 0) {
                    trace.simpleLog("No Urgent Plans - Waiting");
                    break;
                }
                // 현재 사용 가능한 SseEmitter 객체가 있는지 확인
                if (!clients.containsKey(memberId)) {
                    waitCnt++;
                    trace.simpleLog("Cannot Find Client ID - Waiting");
                    if (waitCnt >= 5) break;
                    continue;
                }
                SseEmitter client = clients.get(memberId);

                // waitTime 계산, 기다리기, 메세지 전송
                try {
                    LocalDateTime lst = LocalDateTime.parse(cacheService.get(String.valueOf(memberId), LocalDateTime.MIN.toString()));
                    if (lst.equals(LocalDateTime.MIN)) {
                        trace.simpleLog("First Message Transfer");
                        cacheService.set(String.valueOf(memberId), LocalDateTime.now().toString());
                        client.send(SseEmitter.event()
                                .name("message")
                                .data(new ObjToJsonConverter().convert(new UrgentMsgInfoVO(plans.size(), plans.get(0).getId()))));
                    } else {
                        LocalDateTime before_wait = LocalDateTime.now();
                        long wt = calWaitTime(lst, before_wait, alarm_term);
                        log.info("wt : {}", wt);
                        if (wt > 8500) {
                            Thread.sleep(8500);
                            client.send(SseEmitter.event()
                                    .name("dummy")
                                    .data("waiting"));
                        } else {
                            Thread.sleep(wt);
                            LocalDateTime after_wait = LocalDateTime.now();
                            trace.intervalLog("Message Transfer", lst, after_wait, alarm_term);
                            cacheService.set(String.valueOf(memberId), LocalDateTime.now().toString());
                            client.send(SseEmitter.event()
                                    .name("message")
                                    .data(new ObjToJsonConverter().convert(new UrgentMsgInfoVO(plans.size(), plans.get(0).getId()))));
                        }
                    }
                } catch (ClientAbortException cae) {
                    log.info("I caught ClientAbortException!");
                    break;
                } catch (InterruptedException ire) {
                    log.info("I caught InterruptedException!");
                } catch (IOException ioe) {
                    log.info("I caught IOException!");
                }

            }
            able = true;
        }

        private long calWaitTime(LocalDateTime msgLastSentTime, LocalDateTime now, int alarm_term) {
            long duration = Duration.between(msgLastSentTime, now).toMillis();
            long alarm_millis = alarm_term * 60000L;
            if (duration < alarm_millis) {
                LocalDateTime expectedTime = msgLastSentTime.plusMinutes(alarm_term);
                return Duration.between(now, expectedTime).toMillis();
            } else {
                return (alarm_millis - duration % alarm_millis);
            }
        }
    }
}

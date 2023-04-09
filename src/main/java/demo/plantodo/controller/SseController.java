package demo.plantodo.controller;
import demo.plantodo.VO.UrgentMsgInfoVO;
import demo.plantodo.converter.ObjToJsonConverter;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.logger.SseTrace;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequestMapping("/sse")
@RestController
@RequiredArgsConstructor
public class SseController {
    private SseTrace trace = new SseTrace();
    private final MemberService memberService;
    private final PlanService planService;

    private final RedisClient redisClient;

    private static final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

//    @GetMapping("/last")
//    public LocalDateTime getLastSentTime(HttpServletRequest request) {
//        Long memberId = memberService.getMemberId(request);
//        return lastSentTimeRec.get(memberId);
//    }

    void setLastSentTime(Long memberId, LocalDateTime time) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        commands.set(String.valueOf(memberId), time.toString());
        connection.close();
    }

    LocalDateTime getLastSentTime(Long memberId) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        String s = commands.get(String.valueOf(memberId));
        connection.close();
        return LocalDateTime.parse(s);
    }

    void updateLastSentTime(Long memberId, LocalDateTime time) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        commands.del(String.valueOf(memberId));
        commands.set(String.valueOf(memberId), time.toString());
    }

    @GetMapping("/subscribe")
    public SseEmitter subscribe(HttpServletRequest request) throws IOException {
        Long memberId = memberService.getMemberId(request);

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        clients.put(memberId, emitter);
        SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                .id(String.valueOf(memberId))
                .name("dummy")
                .data("EventStream Created. This is dummy data of [userId : " + memberId + "]");

        setLastSentTime(memberId, LocalDateTime.MIN);
        emitter.send(initEvent);
        emitter.onTimeout(() -> clients.remove(memberId));
        emitter.onCompletion(() -> clients.remove(memberId));
        return emitter;
    }

    @GetMapping("/sendAlarm")
    public void sendAlarm(HttpServletRequest request) {
        Long memberId = memberService.getMemberId(request);
        int deadline_alarm_term = memberService.findOne(memberId).getSettings().getDeadline_alarm_term();
        /*실험용 (나중에 new ArrayList<>() 차이에 Plan 리스트를 조회해서 넣어야 함*/
        Thread loginThread = new Thread(new SendAlarmRunnable(planService, memberId, deadline_alarm_term));
        loginThread.setName("loginThread" + memberId);
        loginThread.start();
    }

    @GetMapping("/quitAlarm")
    public void quitAlarm(HttpServletRequest request) {
        Long memberId = memberService.getMemberId(request);
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

            while (!Thread.interrupted()) {
                List<PlanTerm> plans = planService.findUrgentPlans(memberId);
                if (plans.size() == 0) {
                    trace.simpleLog("No Urgent Plans - Waiting");
                    break;
                }
                if (!clients.containsKey(memberId)) {
                    trace.simpleLog("Cannot Find Client ID - Waiting");
                    break;
                }
                SseEmitter client = clients.get(memberId);
                try {
                    client.send(new ObjToJsonConverter().convert(new UrgentMsgInfoVO(plans.size(), plans.get(0).getId())));
                    LocalDateTime msgLastSentTime = getLastSentTime(memberId);
                    LocalDateTime now = LocalDateTime.now();
                    if (msgLastSentTime.isEqual(LocalDateTime.MIN)) {
                        trace.simpleLog("First Message Transfer");
                    } else {
                        trace.intervalLog("Message Transfer", msgLastSentTime, alarm_term);
                    }
                    updateLastSentTime(memberId, now);
                    Thread.sleep(alarm_term*60000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    break;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    break;
                }
            }
        }
    }

}

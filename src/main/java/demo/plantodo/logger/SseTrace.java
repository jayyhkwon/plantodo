package demo.plantodo.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class SseTrace implements Trace {

    final Logger logger = LoggerFactory.getLogger(SseTrace.class);
    public SseTrace() {
    }

    @Override
    public void simpleLog(String msg) {
        logger.info("[{}]", msg);
    }

    @Override
    public void complexLog(String msg, String type, String detail) {
        if (detail.equals("")) simpleLog(msg + " (" + type + ")");
        else simpleLog(msg + " (" + type + ") (" + detail + ")");
    }

    private String parseDetail(LocalDateTime msgLastSentTime, long expected, long result) {
        return "MsgLastSentTime : " + msgLastSentTime + " / Current Time : " + LocalDateTime.now() + " / Expected : " + String.valueOf(expected) + " / Result : " + String.valueOf(result);
    }

    public void intervalLog(String msg, LocalDateTime msgLastSentTime, int deadline_alarm_term) {
        long dm = deadline_alarm_term * 60000L;
        long dur = Duration.between(msgLastSentTime, LocalDateTime.now()).toMillis();
        if (dm-100 <= dur && dur <= dm+100) complexLog(msg, "Valid", parseDetail(msgLastSentTime, dm, dur));
        else complexLog(msg, "Invalid", parseDetail(msgLastSentTime, dm, dur));
    }
}

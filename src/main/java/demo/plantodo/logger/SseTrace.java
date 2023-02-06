package demo.plantodo.logger;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

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

    private String parseDetail(LocalDateTime msgLastSentTime, double expected, double result) {
        return "MsgLastSentTime : " + msgLastSentTime + " / Current Time : " + LocalDateTime.now() + " / Expected : " + String.valueOf(expected) + " / Result : " + String.valueOf(result);
    }

    public void intervalLog(String msg, LocalDateTime msgLastSentTime, int deadline_alarm_term) {
        long dur = Duration.between(msgLastSentTime, LocalDateTime.now()).toMillis();
        long ratio = dur / (deadline_alarm_term * 60000);
        if (ratio == deadline_alarm_term) complexLog(msg, "Valid", "");
        else complexLog(msg, "Invalid", parseDetail(msgLastSentTime, deadline_alarm_term, ratio));
    }
}

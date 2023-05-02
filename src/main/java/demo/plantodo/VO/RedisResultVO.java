package demo.plantodo.VO;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RedisResultVO {
    private Long traceId;
    private LocalDateTime msgLastSentTime;
    private Long waitTime;
    private List result;

    public RedisResultVO(Long traceId, LocalDateTime msgLastSentTime, Long waitTime, List result) {
        this.traceId = traceId;
        this.msgLastSentTime = msgLastSentTime;
        this.waitTime = waitTime;
        this.result = result;
    }
}

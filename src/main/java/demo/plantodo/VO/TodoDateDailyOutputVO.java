package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class TodoDateDailyOutputVO {
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate searchDate;
    private Long todoDateId;

    public TodoDateDailyOutputVO(LocalDate searchDate, Long todoDateId) {
        this.searchDate = searchDate;
        this.todoDateId = todoDateId;
    }
}

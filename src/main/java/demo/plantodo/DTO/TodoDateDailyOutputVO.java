package demo.plantodo.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class TodoDateDailyOutputVO {
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate searchDate;
    private Long todoDateId;
}

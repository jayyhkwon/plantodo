package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class TodoDateDailyInputVO {
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate selectedDailyDate;
    private Long planId;
    private String title;
}

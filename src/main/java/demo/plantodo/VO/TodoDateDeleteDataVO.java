package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class TodoDateDeleteDataVO {
    private String pageInfo;
    @DateTimeFormat(pattern = "yyyy-MM-dd") private LocalDate selectedDate;
    private Long planId;
    private Long todoDateId;
}

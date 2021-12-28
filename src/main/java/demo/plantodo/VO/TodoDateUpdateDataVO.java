package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class TodoDateUpdateDataVO {
    private String pageInfo;
    private @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate selectedDate;
    private Long planId;
    private Long todoDateId;
    private String updateTitle;
}

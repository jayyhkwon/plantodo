package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class TodoDateResHomeVO {
    private String pageInfo;
    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchDate;
}

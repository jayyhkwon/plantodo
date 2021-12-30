package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter @Setter
public class DateSearchForm {
    private Long planId;

    @NotNull(message = "시작일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}

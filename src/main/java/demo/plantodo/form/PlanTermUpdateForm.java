package demo.plantodo.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
public class PlanTermUpdateForm {

    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String endTime;

    /*테스트용 생성자*/
    public PlanTermUpdateForm(String title, LocalDate startDate, LocalDate endDate, String endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endTime = endTime;
    }
}

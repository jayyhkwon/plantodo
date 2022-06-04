package demo.plantodo.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.*;

@Getter @Setter
@NoArgsConstructor
public class PlanTermRegisterForm {

    @NotBlank
    private String title;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Nullable
    private String endTime;

    /*테스트용 생성자*/
    public PlanTermRegisterForm(String title, LocalDate startDate, LocalDate endDate, @Nullable String endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endTime = endTime;
    }
}

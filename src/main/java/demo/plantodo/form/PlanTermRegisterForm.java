package demo.plantodo.form;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
public class PlanTermRegisterForm {

    private String startDate;

    private String endDate;

    private String title;
}

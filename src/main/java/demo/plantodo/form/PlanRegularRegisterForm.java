package demo.plantodo.form;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlanRegularRegisterForm {
    @NotBlank
    private String title;
}

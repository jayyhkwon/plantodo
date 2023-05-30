package demo.plantodo.form;

import demo.plantodo.domain.Plan;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class TodoRegisterForm {
    private String title;
    private int repOption;
    private Long planId;
    private List<String> repValue;
}

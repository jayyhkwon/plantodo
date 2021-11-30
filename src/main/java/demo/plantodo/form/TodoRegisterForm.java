package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TodoRegisterForm {
    private String title;
    private int repOption;
    private Long planId;
    private List<String> repValue;
}

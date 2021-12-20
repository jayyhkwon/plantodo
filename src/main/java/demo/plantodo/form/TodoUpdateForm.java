package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TodoUpdateForm {
    private Long planId;
    private Long todoId;
    private String title;
    private int repOption;
    private List<String> repValue;

    public TodoUpdateForm() {
    }

    public TodoUpdateForm(Long planId, Long todoId, String title, int repOption, List<String> repValue) {
        this.planId = planId;
        this.todoId = todoId;
        this.title = title;
        this.repOption = repOption;
        this.repValue = repValue;
    }
}


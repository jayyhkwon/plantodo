package demo.plantodo.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TodoButtonVO {
    private Long planId;
    private Long todoId;

    public TodoButtonVO() {
    }

    public TodoButtonVO(Long planId, Long todoId) {
        this.planId = planId;
        this.todoId = todoId;
    }
}

package demo.plantodo.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TodoButtonDTO {
    private Long planId;
    private Long todoId;

    public TodoButtonDTO() {
    }

    public TodoButtonDTO(Long planId, Long todoId) {
        this.planId = planId;
        this.todoId = todoId;
    }
}

package demo.plantodo.VO;

import demo.plantodo.domain.TodoDate;
import lombok.Getter;

@Getter
public class TodoDateVO {
    private long id;
    private String title;
    private String dType;
    private String todoStatus;
    private String planStatus;

    public TodoDateVO(long id, String title, String dType, String todoStatus, String planStatus) {
        this.id = id;
        this.title = title;
        this.dType = dType;
        this.todoStatus = todoStatus;
        this.planStatus = planStatus;
    }
}

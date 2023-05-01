package demo.plantodo.VO;

import demo.plantodo.domain.Todo;
import lombok.Getter;

import java.util.List;

@Getter
public class TodoDetailVO {
    private long id;
    private String title;
    private int repOption;
    private List<String> repValue;

    public TodoDetailVO(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.repOption = todo.getRepOption();
        this.repValue = todo.getRepValue();
    }

    public TodoDetailVO(long id, String title, int repOption, List<String> repValue) {
        this.id = id;
        this.title = title;
        this.repOption = repOption;
        this.repValue = repValue;
    }
}

package demo.plantodo.VO;

import demo.plantodo.domain.Todo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        this.repValue = this.repOption == 1 ? sortRepVal(todo.getRepValue()) : todo.getRepValue();
    }
    private List<String> sortRepVal(List<String> repValue) {
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        return Arrays.stream(days).filter(repValue::contains).collect(Collectors.toList());
    }
}

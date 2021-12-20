package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class TodoDate {
    @Id @GeneratedValue
    @Column(name = "todo_date_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Enumerated(value = EnumType.STRING)
    private TodoStatus todoStatus;

    private LocalDate dateKey;

//    @ElementCollection(fetch = FetchType.LAZY)
//    private List<String> comments = new ArrayList<String>();

    public TodoDate() {
    }

    public TodoDate(Todo todo, TodoStatus todoStatus, LocalDate dateKey) {
        this.todo = todo;
        this.todoStatus = todoStatus;
        this.dateKey = dateKey;
    }

    public void swtichStatus() {
        if (this.todoStatus.equals(TodoStatus.CHECKED)) {
            this.todoStatus = TodoStatus.UNCHECKED;
        } else {
            this.todoStatus = TodoStatus.CHECKED;
        }
    }

}

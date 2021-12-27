package demo.plantodo.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@DiscriminatorValue("Rep")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDateRep extends TodoDate {

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Builder
    public TodoDateRep(TodoStatus todoStatus, LocalDate dateKey, Todo todo) {
        super(todoStatus, dateKey);
        this.title = todo.getTitle();
        this.todo = todo;
    }
}

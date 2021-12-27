package demo.plantodo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;

@Slf4j
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDate {
    @Id @GeneratedValue
    @Column(name = "todo_date_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private TodoStatus todoStatus;

    private LocalDate dateKey;

    @Column(insertable=false, updatable=false)
    private String dtype;

    public TodoDate(TodoStatus todoStatus, LocalDate dateKey) {
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

package demo.plantodo.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@DiscriminatorValue("Daily")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoDateDaily extends TodoDate {

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Builder
    public TodoDateDaily(TodoStatus todoStatus, LocalDate dateKey, String title, Plan plan) {
        super(todoStatus, dateKey);
        this.title = title;
        this.plan = plan;
    }
}

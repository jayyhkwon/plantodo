package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class TodoDateComment {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_date_id")
    private TodoDate todoDate;

    private String comment;

    public TodoDateComment() {
    }

    public TodoDateComment(TodoDate todoDate, String comment) {
        this.todoDate = todoDate;
        this.comment = comment;
    }
}

package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class Todo {
    @Id @GeneratedValue
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Enumerated(value = EnumType.STRING)
    private TodoStatus todoStatus;

    private String title;

    private int repOption;

    @ElementCollection(fetch = LAZY)
    private Set<String> repValue = new HashSet<String>();

    public Todo() {
    }

    public Todo(Member member, Plan plan, TodoStatus todoStatus, String title, int repOption, Set<String> repValue) {
        this.member = member;
        this.plan = plan;
        this.todoStatus = todoStatus;
        this.title = title;
        this.repOption = repOption;
        this.repValue = repValue;
    }
}

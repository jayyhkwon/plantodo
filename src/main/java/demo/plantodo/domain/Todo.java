package demo.plantodo.domain;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
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

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Plan getPlan() {
        return plan;
    }

    public TodoStatus getTodoStatus() {
        return todoStatus;
    }

    public String getTitle() {
        return title;
    }

    public int getRepOption() {
        return repOption;
    }
}

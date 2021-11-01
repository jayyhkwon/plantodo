package demo.plantodo.domain;

import javax.persistence.*;
import java.util.ArrayList;

import static javax.persistence.FetchType.LAZY;

@Entity
public class Plan {
    @Id @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private PlanStatus planStatus;

    @Embedded
    private Period period;

    private String title;

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public PlanStatus getPlanStatus() {
        return planStatus;
    }

    public Period getPeriod() {
        return period;
    }

    public String getTitle() {
        return title;
    }
}

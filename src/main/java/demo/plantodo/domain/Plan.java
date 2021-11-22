package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Plan {
    @Id @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus;

    @Embedded
    private Period period;

    private String title;

    public Plan() {
    }

    public Plan(Member member, PlanStatus planStatus, Period period, String title) {
        this.member = member;
        this.planStatus = planStatus;
        this.period = period;
        this.title = title;
    }

}

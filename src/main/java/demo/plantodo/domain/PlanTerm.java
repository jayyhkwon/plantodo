package demo.plantodo.domain;

import lombok.Getter;

import javax.persistence.*;

import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class PlanTerm {
    @Id @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus;

    private LocalDate startDate;

    private LocalDate endDate;

    private String title;

    public PlanTerm() {
    }

    public PlanTerm(Member member, PlanStatus planStatus, LocalDate startDate, LocalDate endDate, String title) {
        this.member = member;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }
}

package demo.plantodo.domain;

import demo.plantodo.converter.StringToLocalDateConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class Plan {
    @Id @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus;

    private String startDate;

    private String endDate;

    private String title;

    public Plan() {
    }

    public Plan(Member member, PlanStatus planStatus, String startDate, String endDate, String title) {
        this.member = member;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
    }
}

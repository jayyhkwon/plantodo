package demo.plantodo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {
    @Id @GeneratedValue
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PlanStatus planStatus;

    private LocalDate startDate;
    private String title;

    @Column(insertable=false, updatable=false)
    private String dtype;


    public Plan(Member member, PlanStatus planStatus, LocalDate startDate, String title) {
        this.member = member;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.title = title;
    }
}

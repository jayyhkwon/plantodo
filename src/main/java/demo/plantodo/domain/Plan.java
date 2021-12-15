package demo.plantodo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Slf4j
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter @Setter
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

    /*비즈니스 로직*/
    public void changeToPast() {
        if (this.planStatus.equals(PlanStatus.NOW) || this.planStatus.equals(PlanStatus.COMPLETED)) {
            this.planStatus = PlanStatus.PAST;
        } else {
            throw new IllegalStateException();
        }
    }

    public void switchCompleteToNow() {
        if (this.planStatus.equals(PlanStatus.COMPLETED)) {
            this.planStatus = PlanStatus.NOW;
        } else if (this.planStatus.equals(PlanStatus.NOW)) {
            this.planStatus = PlanStatus.COMPLETED;
        } else {
            throw new IllegalStateException();
        }
    }

}

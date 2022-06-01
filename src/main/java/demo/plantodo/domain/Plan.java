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

    @Column(name = "checked_tododate_cnt")
    private int checked_TodoDate_cnt;

    @Column(name = "unchecked_tododate_cnt")
    private int unchecked_TodoDate_cnt;

    public Plan(Member member, PlanStatus planStatus, LocalDate startDate, String title) {
        this.member = member;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.title = title;
        this.checked_TodoDate_cnt = 0;
        this.unchecked_TodoDate_cnt = 0;
    }

    /*비즈니스 로직*/

    /*unchecked (todoDate 등록)*/
    public void addUnchecked(int uncheckedCnt) {
        this.unchecked_TodoDate_cnt += uncheckedCnt;
    }

    /*checked- unchecked- (to-do 삭제)*/
    public void deleteCheckedAndUnchecked(int uncheckedCnt, int checkedCnt) {
        this.unchecked_TodoDate_cnt -= uncheckedCnt;
        this.checked_TodoDate_cnt -= checkedCnt;
    }

    /*checked-1 unchecked+1 (todoDate 상태 변경)*/
    public void exchangeCheckedToUnchecked(int uncheckedCnt, int checkedCnt) {
        this.checked_TodoDate_cnt += uncheckedCnt;
        this.unchecked_TodoDate_cnt += checkedCnt;

    }

    public void changeToDeleted() {
        if (this.planStatus.equals(PlanStatus.NOW) || this.planStatus.equals(PlanStatus.COMPLETED)) {
            this.planStatus = PlanStatus.DELETED;
        } else {
            throw new IllegalStateException();
        }
    }

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

    // 달성도 계산 로직
    public int calculate_plan_compPercent() {
        float checkedCnt = this.getChecked_TodoDate_cnt();
        float uncheckedCnt = this.getUnchecked_TodoDate_cnt();
        float tmp = (checkedCnt / (uncheckedCnt + checkedCnt)) * 100;
        int compPercent = Math.round(tmp*100)/100;
        return compPercent;
    }
}

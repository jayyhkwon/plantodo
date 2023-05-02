package demo.plantodo.VO;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanTerm;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class PlanDetailVO {
    private long id;
    private String dType;
    private String title;
    private String planStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime endTime;
    private boolean emphasis;
    private int compPercent;

    public PlanDetailVO(Plan plan, LocalDate endDate) {
        this.id = plan.getId();
        this.dType = plan.getDtype();
        this.title = plan.getTitle();
        this.planStatus = plan.getPlanStatus().toString();
        this.startDate = plan.getStartDate();
        this.endDate = endDate;
        this.emphasis = plan.isEmphasis();
        if (plan instanceof PlanTerm) this.endTime = ((PlanTerm) plan).getEndTime();
        else this.endTime = LocalTime.of(23, 59);
        this.compPercent = plan.calculate_plan_compPercent();
    }

    public PlanDetailVO(long id, String title, String planStatus, LocalDate startDate, LocalDate endDate, boolean emphasis) {
        this.id = id;
        this.title = title;
        this.planStatus = planStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.emphasis = emphasis;
    }
}

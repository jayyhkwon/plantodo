package demo.plantodo.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.PlanStatus;
import demo.plantodo.domain.PlanTerm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class PlanListVO {
    private Long id;
    private PlanStatus planStatus;
    private boolean emphasis;
    private String dtype;

    @JsonFormat
    private LocalDate startDate;

    @JsonFormat
    private LocalDate endDate;

    @JsonFormat
    private LocalTime endTime;

    private String title;
    private int compPercent;

    public PlanListVO(PlanTerm plan) {
        this.id = plan.getId();
        this.planStatus = plan.getPlanStatus();
        this.emphasis = plan.isEmphasis();
        this.dtype = plan.getDtype();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.endTime = plan.getEndTime();
        this.title = plan.getTitle();
        this.compPercent = plan.calculate_plan_compPercent();
    }

    public PlanListVO(PlanRegular plan) {
        this.id = plan.getId();
        this.planStatus = plan.getPlanStatus();
        this.emphasis = plan.isEmphasis();
        this.startDate = plan.getStartDate();
        this.dtype = plan.getDtype();
        this.title = plan.getTitle();
        this.compPercent = plan.calculate_plan_compPercent();
    }
}

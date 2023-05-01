package demo.plantodo.VO;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanStatus;
import lombok.Getter;

@Getter
public class PlanHomeVO {
    private long id;
    private String title;
    private String planStatus;

    public PlanHomeVO(Plan plan) {
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.planStatus = plan.getPlanStatus().toString();
    }
}

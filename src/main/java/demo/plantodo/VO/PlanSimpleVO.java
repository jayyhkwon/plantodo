package demo.plantodo.VO;

import demo.plantodo.domain.Plan;
import lombok.Getter;

@Getter
public class PlanSimpleVO {
    private Long id;
    private String title;

    public PlanSimpleVO(Plan plan) {
        this.id = plan.getId();
        this.title = plan.getTitle();
    }
}

package demo.plantodo.VO;

import lombok.Getter;

@Getter
public class PlanHomeVO {
    private long id;
    private String title;
    private String planStatus;

    public PlanHomeVO(long id, String title, String planStatus) {
        this.id = id;
        this.title = title;
        this.planStatus = planStatus;
    }
}

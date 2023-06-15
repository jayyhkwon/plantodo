package demo.plantodo.VO;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TodoDateResPlanVO {
    private String pageInfo;
    private Long planId;

    public TodoDateResPlanVO(String pageInfo, Long planId) {
        this.pageInfo = pageInfo;
        this.planId = planId;
    }
}

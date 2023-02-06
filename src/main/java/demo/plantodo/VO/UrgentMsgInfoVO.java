package demo.plantodo.VO;

import demo.plantodo.domain.PlanTerm;
import lombok.Getter;

import java.util.List;

@Getter
public class UrgentMsgInfoVO {
    private int count;
    private Long planId;

    public UrgentMsgInfoVO(int count, Long planId) {
        this.count = count;
        this.planId = planId;
    }
}

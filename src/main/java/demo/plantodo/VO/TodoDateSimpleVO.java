package demo.plantodo.VO;

import lombok.Getter;

@Getter
public class TodoDateSimpleVO {
    private Long id;

    public TodoDateSimpleVO(Long id) {
        this.id = id;
    }
}

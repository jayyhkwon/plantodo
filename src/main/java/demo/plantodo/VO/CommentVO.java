package demo.plantodo.VO;

import demo.plantodo.domain.TodoDateComment;
import lombok.Getter;

@Getter
public class CommentVO {
    private Long id;
    private String comment;

    public CommentVO(TodoDateComment tdc) {
        this.id = tdc.getId();
        this.comment = tdc.getComment();
    }
}

package demo.plantodo.service;

import demo.plantodo.VO.CommentVO;
import demo.plantodo.domain.TodoDate;
import demo.plantodo.domain.TodoDateComment;
import demo.plantodo.repository.CommentRepository;
import demo.plantodo.repository.TodoDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoDateRepository todoDateRepository;

    public List<TodoDateComment> getCommentsByTodoDateId(Long todoDateId) {
        return commentRepository.getCommentsByTodoDateId(todoDateId);
    }

    public void save(Long todoDateId, String comment) {
        TodoDate todoDate = todoDateRepository.findOne(todoDateId);
        TodoDateComment todoDateComment = new TodoDateComment(todoDate, comment);
        commentRepository.save(todoDateComment);
    }

    public void deleteCommentByTodoDateId(Long todoDateId) {
        List<TodoDateComment> comments = commentRepository.getCommentsByTodoDateId(todoDateId);
        comments.forEach(comment -> commentRepository.delete(comment.getId()));
    }

    public void delete(Long commentId) {
        commentRepository.delete(commentId);
    }

    public void update(Long commentId, String updatedComment) {
        commentRepository.update(commentId, updatedComment);
    }

    public TodoDateComment findOne(Long commentId) {
        return commentRepository.findOne(commentId);
    }

    public List<TodoDateComment> findAllCommentsByTodoDateId(Long todoDateId) {
        // 테스트용
        return commentRepository.getCommentsByTodoDateId(todoDateId);
    }

    public List<CommentVO> getCommentVOByTodoDateId(Long todoDateId) {
        return commentRepository.getCommentsByTodoDateId(todoDateId).stream().map(CommentVO::new).collect(Collectors.toList());
    }
}

package demo.plantodo.service;

import demo.plantodo.domain.TodoDate;
import demo.plantodo.domain.TodoDateComment;
import demo.plantodo.repository.CommentRepository;
import demo.plantodo.repository.TodoDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void delete(Long commentId) {
        commentRepository.delete(commentId);
    }

    public void update(Long commentId, String updatedComment) {
        commentRepository.update(commentId, updatedComment);
    }

    public TodoDateComment findOne(Long commentId) {
        return commentRepository.findOne(commentId);
    }
}

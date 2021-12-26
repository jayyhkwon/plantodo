package demo.plantodo.repository;

import demo.plantodo.domain.TodoDateComment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class CommentRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(TodoDateComment todoDateComment) {
        em.persist(todoDateComment);
    }

    public TodoDateComment findOne(Long commentId) {
        return em.find(TodoDateComment.class, commentId);
    }

    public List<TodoDateComment> getCommentsByTodoDateId(Long todoDateId) {
        return em.createQuery("select c from TodoDateComment c where c.todoDate.id=:todoDateId")
                .setParameter("todoDateId", todoDateId).getResultList();
    }

    public void delete(Long commentId) {
        TodoDateComment comment = findOne(commentId);
        em.remove(comment);
    }

    public void update(Long commentId, String updatedComment) {
        TodoDateComment comment = findOne(commentId);
        comment.setComment(updatedComment);
    }
}

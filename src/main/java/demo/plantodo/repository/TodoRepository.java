package demo.plantodo.repository;

import demo.plantodo.domain.*;
import demo.plantodo.form.TodoUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Transactional
@RequiredArgsConstructor
public class TodoRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Todo todo) {
        em.persist(todo);
    }

    public Todo findOne(Long todoId) {
        return em.find(Todo.class, todoId);
    }

    public List<Todo> getTodoByPlanIdAndDate(Plan plan, LocalDate date) {
        if (plan instanceof PlanRegular) {
            return em.createQuery("select o from Todo o inner join o.plan p where p.id =:planId and treat(p as PlanRegular).startDate <= :date")
                    .setParameter("date", date)
                    .setParameter("planId", plan.getId())
                    .getResultList();
        }
        else {
            return em.createQuery("select o from Todo o inner join o.plan p where p.id =:planId and treat(p as PlanTerm).startDate <= :date and treat(p as PlanTerm).endDate >= :date")
                    .setParameter("date", date)
                    .setParameter("planId", plan.getId())
                    .getResultList();
        }
    }


    public void saveTodoDate(TodoDate todoDate) {
        em.persist(todoDate);
    }

    public TodoDate findOneTodoDate(Long todoDateId) {
        return em.find(TodoDate.class, todoDateId);
    }

    public List<TodoDate> getTodoDateByTodoAndDate(Todo todo, LocalDate searchDate) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId and td.dateKey = :searchDate")
                .setParameter("todoId", todo.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public void switchStatus(Long todoDateId) {
        TodoDate oneTodoDate = findOneTodoDate(todoDateId);
        oneTodoDate.swtichStatus();
    }

    public List<Todo> getTodoByPlanId(Long planId) {
        return em.createQuery("select t from Todo t where t.plan.id = :planId")
                .setParameter("planId", planId)
                .getResultList();
    }

    public List<TodoDate> getTodoDateByTodoId(Long todoId) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId")
                .setParameter("todoId", todoId)
                .getResultList();
    }

    public List<TodoDate> getTodoDateByTodoIdAfterToday(Long todoId, LocalDate today) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId and td.dateKey >= :today")
                .setParameter("todoId", todoId)
                .setParameter("today", today)
                .getResultList();
    }

    public void deleteTodo(Long todoId) {
        Todo todo = findOne(todoId);
        em.remove(todo);
    }

    public void updateTodo(TodoUpdateForm todoUpdateForm, Long todoId) {
        Todo todo = findOne(todoId);
        todo.setTitle(todoUpdateForm.getTitle());
        todo.setRepOption(todoUpdateForm.getRepOption());
        todo.setRepValue(todoUpdateForm.getRepValue());
    }

    public void deleteTodoDate(Long todoDateId) {
        TodoDate todoDate = findOneTodoDate(todoDateId);
        em.remove(todoDate);
    }

    public void updateTodoDate(Todo todo, Long todoDateId) {
        TodoDate todoDate = findOneTodoDate(todoDateId);
        todoDate.setTodo(todo);
    }

    /*comment*/
    public void saveComment(TodoDateComment todoDateComment) {
        em.persist(todoDateComment);
    }

    public TodoDateComment findComment(Long commentId) {
        return em.find(TodoDateComment.class, commentId);
    }

    public List<TodoDateComment> getCommentsByTodoDateId(Long todoDateId) {
        return em.createQuery("select c from TodoDateComment c where c.todoDate.id=:todoDateId")
                .setParameter("todoDateId", todoDateId).getResultList();
    }

    public void deleteComment(Long commentId) {
        TodoDateComment comment = findComment(commentId);
        em.remove(comment);
    }

    public void updateComment(Long commentId, String updatedComment) {
        TodoDateComment comment = findComment(commentId);
        comment.setComment(updatedComment);
    }
}

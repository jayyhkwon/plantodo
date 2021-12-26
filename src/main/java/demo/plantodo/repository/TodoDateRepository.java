package demo.plantodo.repository;

import demo.plantodo.domain.Todo;
import demo.plantodo.domain.TodoDate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class TodoDateRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(TodoDate todoDate) {
        em.persist(todoDate);
    }

    public TodoDate findOne(Long todoDateId) {
        return em.find(TodoDate.class, todoDateId);
    }

    public List<TodoDate> getTodoDateByTodoAndDate(Todo todo, LocalDate searchDate) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId and td.dateKey = :searchDate")
                .setParameter("todoId", todo.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public void switchStatus(Long todoDateId) {
        TodoDate oneTodoDate = findOne(todoDateId);
        oneTodoDate.swtichStatus();
    }

    public void delete(Long todoDateId) {
        TodoDate todoDate = findOne(todoDateId);
        em.remove(todoDate);
    }

    public void update(Todo todo, Long todoDateId) {
        TodoDate todoDate = findOne(todoDateId);
        todoDate.setTodo(todo);
    }

}

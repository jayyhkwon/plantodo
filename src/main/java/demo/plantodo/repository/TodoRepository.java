package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.Todo;
import demo.plantodo.domain.TodoDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

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

/*    public void switchStatus(Long todoId) {
        To-do to-do = findOne(todoId);
        to-do.swtichStatus();
    }*/

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
}

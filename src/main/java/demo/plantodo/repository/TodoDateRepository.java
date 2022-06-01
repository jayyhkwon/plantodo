package demo.plantodo.repository;

import demo.plantodo.domain.*;
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
    public TodoDateDaily findOneDaily(Long todoDateId) {
        return em.find(TodoDateDaily.class, todoDateId);
    }
    public TodoDateRep findOneRep(Long todoDateId) { return em.find(TodoDateRep.class, todoDateId); }


    public List<TodoDate> getTodoDateByTodo(Todo todo) {
        return em.createQuery("select td from TodoDateRep td where td.todo.id = :todoId")
                .setParameter("todoId", todo.getId())
                .getResultList();
    }

    public List<TodoDate> getTodoDateByTodoAndDate(Todo todo, LocalDate searchDate) {
        return em.createQuery("select td from TodoDateRep td where td.todo.id = :todoId and td.dateKey = :searchDate")
                .setParameter("todoId", todo.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public TodoDateRep switchStatusRep(Long todoDateId) {
        TodoDateRep rep = findOneRep(todoDateId);
        rep.swtichStatus();
        return rep;
    }

    public TodoDateDaily switchStatusDaily(Long todoDateId) {
        TodoDateDaily daily = findOneDaily(todoDateId);
        daily.swtichStatus();
        return daily;
    }


    public void updateRep(Todo todo, Long todoDateId) {
        TodoDateRep oneRep = findOneRep(todoDateId);
        oneRep.setTodo(todo);
    }

    public List<TodoDate> getTodoDateByPlanAndDate(Plan plan, LocalDate searchDate) {
        return em.createQuery("select td from TodoDate td where treat(td as TodoDateDaily).plan.id=:planId and td.dateKey=:searchDate")
                .setParameter("planId", plan.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public List<TodoDate> getTodoDateRep_ByTodoAndDate(Todo todo, LocalDate searchDate) {
        return em.createQuery("select td from TodoDate td where treat(td as TodoDateRep).todo.id=:todoId and td.dateKey=:searchDate")
                .setParameter("todoId", todo.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public void updateTitle(Long todoDateId, String updateTitle) {
        TodoDate todoDate = findOne(todoDateId);
        if (todoDate instanceof TodoDateRep) {
            TodoDateRep todoDateRep = (TodoDateRep) todoDate;
            todoDateRep.setTitle(updateTitle);
        } else {
            TodoDateDaily todoDateDaily = (TodoDateDaily) todoDate;
            todoDateDaily.setTitle(updateTitle);
        }
    }


    /* 삭제 */
    public void deleteRep(TodoDateRep todoDateRep) {
        em.remove(todoDateRep);
    }

    public void deleteDaily(TodoDateDaily todoDateDaily) {
        em.remove(todoDateDaily);
    }

}

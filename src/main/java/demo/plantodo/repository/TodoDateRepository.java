package demo.plantodo.repository;

import demo.plantodo.domain.*;
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
public class TodoDateRepository {

    @PersistenceContext
    private final EntityManager em;

    /*등록*/
    public void save(TodoDate todoDate) {
        em.persist(todoDate);
    }


    /*조회*/
    public TodoDate findOne(Long todoDateId) {
        return em.find(TodoDate.class, todoDateId);
    }
    public TodoDateDaily findOneDaily(Long todoDateId) {
        return em.find(TodoDateDaily.class, todoDateId);
    }
    public TodoDateRep findOneRep(Long todoDateId) { return em.find(TodoDateRep.class, todoDateId); }

    public List<TodoDate> getTodoDateByTodo(Todo todo) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId", TodoDate.class)
                .setParameter("todoId", todo.getId())
                .getResultList();
    }

    public List<TodoDateRep> getTodoDateRepByTodoId(Long todoId) {
        return em.createQuery("select td from TodoDateRep td where td.todo.id = :todoId", TodoDateRep.class)
                .setParameter("todoId", todoId)
                .getResultList();
    }

    public List<TodoDateDaily> getTodoDateDailyByPlanId(Long planId) {
        return em.createQuery("select td from TodoDateDaily td where td.plan.id = :planId", TodoDateDaily.class)
                .setParameter("planId", planId)
                .getResultList();
    }

    public List<TodoDate> getTodoDateByTodoAndDate(Todo todo, LocalDate searchDate) {
        return em.createQuery("select distinct tdr from TodoDateRep tdr join fetch tdr.todo td left join fetch td.repValue where tdr.todo.id = :todoId and tdr.dateKey = :searchDate")
                .setParameter("todoId", todo.getId())
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public List<TodoDate> getTodoDateByPlanAndDate(Plan plan, LocalDate searchDate) {
        return em.createQuery("select tdr from TodoDate tdr where treat(tdr as TodoDateDaily).plan.id=:planId and tdr.dateKey=:searchDate")
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

    /*변경*/
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

    public void delete(Long todoDateId) {
        TodoDate todoDate = findOne(todoDateId);
        em.remove(todoDate);
    }
    public void deleteRep(TodoDateRep todoDateRep) {
        em.remove(todoDateRep);
    }

    public void deleteDaily(TodoDateDaily todoDateDaily) {
        em.remove(todoDateDaily);
    }
}

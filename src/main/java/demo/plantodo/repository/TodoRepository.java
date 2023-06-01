package demo.plantodo.repository;

import demo.plantodo.VO.TodoDetailVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.TodoUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Transactional
public class TodoRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Todo todo) {
        em.persist(todo);
    }


    /*조회*/
    public Todo findOne(Long todoId) {
        return em.find(Todo.class, todoId);
    }

    public Todo findOneWithRepValue(Long todoId) {
        return em.createQuery("select t from Todo t inner join fetch t.repValue inner join fetch t.plan where t.id = :todoId", Todo.class)
                .setParameter("todoId", todoId)
                .getSingleResult();
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

    public List<Todo> getTodoByPlanId(Long planId) {
        return em.createQuery("select distinct t from Todo t left join fetch t.repValue where t.plan.id = :planId", Todo.class)
                .setParameter("planId", planId)
                .getResultList();
    }

    public List<TodoDate> getTodoDateByTodoIdAfterToday(Long todoId, LocalDate today) {
        return em.createQuery("select td from TodoDate td where td.todo.id = :todoId and td.dateKey >= :today")
                .setParameter("todoId", todoId)
                .setParameter("today", today)
                .getResultList();
    }

    public TodoUpdateForm getTodoUpdateForm(Long planId, Long todoId) {
        Todo todo = findOneWithRepValue(todoId);
        return new TodoUpdateForm(planId, todo.getId(), todo.getTitle(), todo.getRepOption(), todo.getRepValue());
    }


    public List<TodoDate> getTodoDateRepByTodoId(Long todoId) {
        return em.createQuery("select td from TodoDate td where treat(td as TodoDateRep).todo.id = :todoId")
                .setParameter("todoId", todoId)
                .getResultList();
    }

    public List<TodoDate> findDailiesByPlanIdandDate(Long planId) {
        return em.createQuery("select td from TodoDate td where treat(td as TodoDateDaily).plan.id = :planId")
                .setParameter("planId", planId)
                .getResultList();
    }

    public List<Todo> findAllTodoByMemberId(Long memberId) {
        // 테스트용
        return em.createQuery("select td from Todo td where td.member.id = :memberId", Todo.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    /*수정, 삭제*/

    public void delete(Long todoId) {
        Todo todo = findOne(todoId);
        em.remove(todo);
    }

    public void update(TodoUpdateForm todoUpdateForm, Long todoId) {
        Todo todo = findOne(todoId);
        todo.setTitle(todoUpdateForm.getTitle());
        todo.setRepOption(todoUpdateForm.getRepOption());
        todo.setRepValue(todoUpdateForm.getRepValue());
    }
}

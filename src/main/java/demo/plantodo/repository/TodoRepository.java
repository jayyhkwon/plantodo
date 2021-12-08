package demo.plantodo.repository;

import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.Todo;
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

    public List<Todo> getTodoByPlanId(Long planId) {
        return em.createQuery("select d from Todo d where d.plan.id =:planId")
                .setParameter("planId", planId)
                .getResultList();
    }

    public List<Todo> getTodoByPlanIdAndDate(PlanRegular plan, LocalDate date) {
        Long planId = plan.getId();
        return em.createQuery("select o from Todo o where o.plan.id =:planId and o.plan.startDate <= :date and o.plan.endDate >= :date")
                .setParameter("date", date)
                .setParameter("planId", planId)
                .getResultList();
    }

}

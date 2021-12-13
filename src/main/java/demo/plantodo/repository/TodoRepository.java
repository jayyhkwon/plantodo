package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
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


    public List<Todo> getTodoByPlanIdAndDate(Plan plan, LocalDate date) {
        if (plan.getDtype().equals("Regular")) {
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
}

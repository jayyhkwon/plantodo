package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class PlanRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Plan plan) {
        em.persist(plan);
    }

    // plan title로 plan 가져오기(테스트용)
    public List<Plan> findPlanByTitle(String title) {
        return em.createQuery("select p from Plan p where p.title= :title", Plan.class)
                .setParameter("title", title)
                .getResultList();
    }

}

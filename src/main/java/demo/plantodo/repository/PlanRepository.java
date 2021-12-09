package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.PlanTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class PlanRepository {

    @PersistenceContext
    private final EntityManager em;

    /*저장*/
    public void saveRegular(PlanRegular planRegular) {
        em.persist(planRegular);
    }
    public void saveTerm(PlanTerm planTerm) {
        em.persist(planTerm);
    }

    /*plan 한 개 조회*/
    public Plan findOne(Long id) {
        return em.find(Plan.class, id);
    }

    // memberId를 받아서 해당 member의 모든 plan을 가져오기 (to-do 등록 시 사용)
    public List<Plan> findAllPlan(Long memberId) {
        return em.createQuery("select p from Plan p where p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<PlanTerm> findAllPlanTerm(Long memberId) {
        return em.createQuery("select p from Plan p where type(p) = PlanTerm and p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<PlanRegular> findAllPlanRegular(Long memberId) {
        return em.createQuery("select p from Plan p where type(p) = PlanRegular and p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }


}

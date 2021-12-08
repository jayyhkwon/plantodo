package demo.plantodo.repository;

import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.PlanTerm;
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
    private final EntityManager em;

    public void saveRegular(PlanRegular planRegular) {
        em.persist(planRegular);
    }
    public void saveTerm(PlanTerm planTerm) {
        em.persist(planTerm);
    }

    // plan title로 plan 가져오기 (테스트용)
    public List<PlanRegular> findPlanByTitle(String title) {
        return em.createQuery("select p from PlanRegular p where p.title= :title", PlanRegular.class)
                .setParameter("title", title)
                .getResultList();
    }

    // memberId를 받아서 해당 member의 모든 plan을 가져오기
    public List<PlanRegular> findAllPlanRegular(Long memberId) {
        return em.createQuery("select p from PlanRegular p where p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<PlanTerm> findAllPlanTerm(Long memberId) {
        return em.createQuery("select p from PlanTerm p where p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public PlanRegular findOne(Long id) {
        return em.find(PlanRegular.class, id);
    }

}

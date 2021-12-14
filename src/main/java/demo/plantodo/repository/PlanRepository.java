package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.PlanStatus;
import demo.plantodo.domain.PlanTerm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
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

    /*삭제*/
    public void remove(Plan plan) {
        em.remove(plan);
    }

    /*plan 한 개 조회*/
    public Plan findOne(Long id) {
        return em.find(Plan.class, id);
    }

    // memberId를 받아서 해당 member의 모든 plan을 가져오기 (to-do 등록 시 사용)
    public List<Plan> findAllPlan(Long memberId) {
        List<Plan> planList = em.createQuery("select p from Plan p where p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
        int finishedCnt = makeOutdatedPlansCompleted(planList);
        if (finishedCnt == 0) {
            return planList;
        }
        return findAllPlan(memberId);
    }

    public int makeOutdatedPlansCompleted(List<Plan> planList) {
        int finishedCnt = 0;
        for (Plan plan : planList) {
            if (checkPlanTermCompleted(plan) && plan.getPlanStatus() == PlanStatus.NOW) {
                Plan needsFinish = findOne(plan.getId());
                needsFinish.changeStatus();
                finishedCnt += 1;
            }
        }
        return finishedCnt;
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

    public boolean checkPlanTermCompleted(Plan plan) {
        LocalDate today = LocalDate.now();
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            if (today.isAfter(planTerm.getEndDate())) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void updateStatus(Long planId) {
        Plan plan = findOne(planId);
        plan.changeStatus();
    }
}

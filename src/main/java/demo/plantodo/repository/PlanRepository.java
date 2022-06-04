package demo.plantodo.repository;

import demo.plantodo.domain.Plan;
import demo.plantodo.domain.PlanRegular;
import demo.plantodo.domain.PlanStatus;
import demo.plantodo.domain.PlanTerm;
import demo.plantodo.form.PlanRegularUpdateForm;
import demo.plantodo.form.PlanTermUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
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

    /*조회*/
    public Plan findOne(Long id) {
        return em.find(Plan.class, id);
    }

    // memberId를 받아서 해당 member의 모든 plan을 가져오기 (to-do 등록 시 사용)
    public List<Plan> findAllPlan(Long memberId) {
        List<Plan> planList = em.createQuery("select p from Plan p where p.member.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
        int finishedCnt = makeOutdatedPlansPast(planList);
        if (finishedCnt == 0) {
            return planList;
        }
        return findAllPlan(memberId);
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

    public int makeOutdatedPlansPast(List<Plan> planList) {
        int finishedCnt = 0;
        for (Plan plan : planList) {
            if (checkPlanTermCompleted(plan) && plan.getPlanStatus() == PlanStatus.NOW) {
                Plan needsFinish = findOne(plan.getId());
                needsFinish.changeToPast();
                finishedCnt += 1;
            }
        }
        return finishedCnt;
    }

    public boolean checkPlanTermCompleted(Plan plan) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            LocalDate endDate = planTerm.getEndDate();
            LocalTime endTime = planTerm.getEndTime();

            if (plan.getPlanStatus().equals(PlanStatus.NOW)) {
                if (today.isAfter(endDate)) {
                    return true;
                }

                if (today.isEqual(endDate) && now.isAfter(endTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*수정*/
    public void updateStatus(Long planId) {
        Plan plan = findOne(planId);
        plan.switchCompleteToNow();
    }

    public void updateRegular(PlanRegularUpdateForm planRegularUpdateForm, Long planId) {
        Plan planRegular = findOne(planId);
        planRegular.setTitle(planRegularUpdateForm.getTitle());
    }

    public void updateTerm(PlanTermUpdateForm planTermUpdateForm, Long planId) {
        PlanTerm planTerm = (PlanTerm) findOne(planId);
        planTerm.setTitle(planTermUpdateForm.getTitle());
        planTerm.setStartDate(planTermUpdateForm.getStartDate());
        planTerm.setEndDate(planTermUpdateForm.getEndDate());
        planTerm.setEndTime(LocalTime.parse(planTermUpdateForm.getEndTime()));
    }

    public void updateStatusDeleted(Long planId) {
        Plan plan = findOne(planId);
        plan.changeToDeleted();
    }

    public void switchPlanEmphasis(Long planId) {
        Plan plan = findOne(planId);
        plan.switchEmphasis();
    }

    /*plan register용 메서드 (상태가 now인 것만 검색)*/
    public List<Plan> findAllPlanForPlanRegister(Long memberId) {
        return em.createQuery("select p from Plan p where p.member.id=:memberId and p.planStatus = :now")
                .setParameter("memberId", memberId)
                .setParameter("now", PlanStatus.NOW)
                .getResultList();
    }

    /*checkedCnt / uncheckedCnt 변경 메서드*/
    public void addUnchecked(Plan plan, int uncheckedTodoDateCnt) {
        plan.addUnchecked(uncheckedTodoDateCnt);
    }
    public void addUnchecked(Long planId, int uncheckedTodoDateCnt) {
        Plan plan = findOne(planId);
        plan.addUnchecked(uncheckedTodoDateCnt);
    }

    public void deleteCheckedAndUnchecked(Long planId, int checkedCnt, int uncheckedCnt) {
        Plan plan = findOne(planId);
        plan.deleteCheckedAndUnchecked(uncheckedCnt, checkedCnt);
    }

    public void exchangeCheckedToUnchecked(Long planId, int uncheckedCnt, int checkedCnt) {
        Plan plan = findOne(planId);
        plan.exchangeCheckedToUnchecked(uncheckedCnt, checkedCnt);
    }

}

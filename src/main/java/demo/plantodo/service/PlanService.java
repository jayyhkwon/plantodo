package demo.plantodo.service;

import demo.plantodo.domain.*;
import demo.plantodo.form.PlanRegularUpdateForm;
import demo.plantodo.form.PlanTermUpdateForm;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {
    private final TodoService todoService;
    private final PlanRepository planRepository;
    private final TodoDateService todoDateService;

    /*등록*/
    public void saveRegular(PlanRegular planRegular) {
        planRepository.saveRegular(planRegular);
    }
    public void saveTerm(PlanTerm planTerm) {
        planRepository.saveTerm(planTerm);
    }

    /*조회*/
    public Plan findOne(Long id) {
        return planRepository.findOne(id);
    }

    public List<Plan> findAllPlan(Long memberId) {
        return planRepository.findAllPlan(memberId);
    }

    public List<PlanTerm> findAllPlanTerm(Long memberId) {
        return planRepository.findAllPlanTerm(memberId);
    }

    public List<PlanRegular> findAllPlanRegular(Long memberId) {
        return planRepository.findAllPlanRegular(memberId);
    }

    /*수정*/
    public void updateStatus(Long planId) {
        planRepository.updateStatus(planId);
    }

    public void updateRegular(PlanRegularUpdateForm planRegularUpdateForm, Long planId) {
        planRepository.updateRegular(planRegularUpdateForm, planId);
    }

    public void updateTerm(PlanTermUpdateForm planTermUpdateForm, Long planId) {
        planRepository.updateTerm(planTermUpdateForm, planId);
    }

    /*삭제*/
    public void delete(Plan plan) {
        List<Todo> todo_list = todoService.getTodoByPlanId(plan.getId());
        int canDeletePlan = 0;
        for (Todo todo : todo_list) {
            canDeletePlan += todoService.delete(todo.getId());
        }
        canDeletePlan += todoDateService.deleteDailyByPlan(LocalDate.now(), plan.getId());
        /*과거 기록이 0개일 경우 plan 자체를 삭제. 과거 기록이 0개가 아닐 경우 plan의 status를 deleted로 변경*/
        if (canDeletePlan != 0) {
            planRepository.updateStatusDeleted(plan.getId());
        } else {
            planRepository.remove(plan);
        }
    }

    public List<Plan> findAllPlanForPlanRegister(Long memberId) {
        return planRepository.findAllPlanForPlanRegister(memberId);
    }

    public List<Plan> findAllPlanForBlock(LocalDate eachDate, Long memberId) {
        /*planTerm인 경우 plan의 StartDate와 endDate 사이에 eachDate가 있어야 한다.*/
        /*planRegular의 경우 plan의 StartDate >= eachDate*/
        ArrayList<Plan> result = new ArrayList<>();
        List<Plan> allPlan = planRepository.findAllPlan(memberId);
        for (Plan plan : allPlan) {
            if (plan.getDtype().equals("Term")) {
                PlanTerm planTerm = (PlanTerm) plan;
                if (eachDate.isBefore(planTerm.getStartDate()) || eachDate.isAfter(planTerm.getEndDate())) {
                    continue;
                }
                result.add(plan);
            } else {
                if (eachDate.isBefore(plan.getStartDate())) {
                    continue;
                }
                result.add(plan);
            }
        }
        return result;
    }
}

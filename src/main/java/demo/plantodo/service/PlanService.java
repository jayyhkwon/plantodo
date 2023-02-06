package demo.plantodo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.plantodo.VO.UrgentMsgInfoVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.PlanRegularUpdateForm;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.form.PlanTermUpdateForm;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
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
    /*PlanController - planRegisterTerm 전용*/
    public void saveTerm(Member member, PlanTermRegisterForm form) {
        planRepository.saveTerm(createPlanTerm(member, form));
    }

    private PlanTerm createPlanTerm(Member member, PlanTermRegisterForm form) {
        LocalTime endTime = form.getEndTime() == "" ? LocalTime.of(23, 59) : LocalTime.parse(form.getEndTime());
        return new PlanTerm(member, PlanStatus.NOW, form.getStartDate(), form.getTitle(), form.getEndDate(), endTime);
    }

    /*조회*/
    public Plan findOne(Long id) {
        return planRepository.findOne(id);
    }

    public List<Plan> findAllPlan(Long memberId) {
        return planRepository.findAllPlan(memberId);
    }

    public HashMap<Plan, Integer> findAllPlan_withCompPercent(Long memberId) {
        /*HashMap -> plan:달성도 계산*/
        HashMap<Plan, Integer> resultMap = new HashMap<>();
        List<Plan> plans = planRepository.findAllPlan(memberId);
        for (Plan plan : plans) {
            /*달성도 계산*/
            int compPercent = plan.calculate_plan_compPercent();
            resultMap.put(plan, compPercent);
        }
        return resultMap;
    }

    public List<PlanTerm> findAllPlanTerm(Long memberId) {
        return planRepository.findAllPlanTerm(memberId);
    }

    public List<PlanRegular> findAllPlanRegular(Long memberId) {
        return planRepository.findAllPlanRegular(memberId);
    }

    public List<PlanTerm> findUrgentPlans(Long memberId) {
        /*혹시 Past상태가 안 된 Plan이 있으면 Past 상태로 + 모든 Plan 조회*/
        List<Plan> plans = planRepository.findAllPlan(memberId);
        /*planTerm 필터링  / NOW 필터링 / 달성도 필터링 / emphasis로 필터링 / 타입을 PlanTerm으로 바꾸기 / endDate 필터링*/
        return plans.stream()
                .filter(p -> p instanceof PlanTerm)
                .filter(p -> p.getPlanStatus().equals(PlanStatus.NOW))
                .filter(p -> p.calculate_plan_compPercent() != 100)
                .filter(Plan::isEmphasis).map(p -> (PlanTerm) p)
                .filter(p -> p.getEndDate().isEqual(LocalDate.now()))
                .sorted(Comparator.comparing(PlanTerm::getEndTime))
                .collect(Collectors.toList());
    }


    /*수정*/
    public void updateStatus(Long planId) {
        planRepository.updateStatus(planId);
    }

    public void updateRegular(PlanRegularUpdateForm planRegularUpdateForm, Long planId) {
        planRepository.updateRegular(planRegularUpdateForm, planId);
    }

    public void updateTerm(PlanTermUpdateForm form, Long planId) {
        planRepository.updateTerm(form, planId);
    }

    public void switchPlanEmphasis(Long planId) {
        planRepository.switchPlanEmphasis(planId);
    }

    /*삭제*/
    public void delete(Long planId) {
        /*To-do 삭제*/
        List<Todo> todo_list = todoService.getTodoByPlanId(planId);
        todo_list.forEach(todo -> todoService.delete(todo.getId()));

        /*TodoDate(Daily) 삭제*/
        todoDateService.deleteDailyByPlanId(planId);

        /*plan 삭제*/
        planRepository.delete(planId);
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

    public void addUnchecked(Plan plan, int uncheckedTodoDateCnt) {
        planRepository.addUnchecked(plan, uncheckedTodoDateCnt);
    }

}

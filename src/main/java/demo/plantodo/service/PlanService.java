package demo.plantodo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.plantodo.VO.PlanListVO;
import demo.plantodo.VO.UrgentMsgInfoVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.PlanRegularUpdateForm;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.form.PlanTermUpdateForm;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoDateRepository;
import demo.plantodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlanService {
    private final TodoService todoService;
    private final TodoRepository todoRepository;
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
        LocalTime endTime = form.getEndTime().equals("") ? LocalTime.of(23, 59) : LocalTime.parse(form.getEndTime());
        return new PlanTerm(member, PlanStatus.NOW, form.getStartDate(), form.getTitle(), form.getEndDate(), endTime);
    }

    /*조회*/
    public Plan findOne(Long id) {
        return planRepository.findOne(id);
    }

    public List<Plan> findAllPlan(Long memberId) {
        return planRepository.findAllPlan(memberId);
    }

    public List<PlanListVO> findAllPlan_withCompPercent(Long memberId) {
        return planRepository.findAllPlan(memberId).stream()
                .sorted(Comparator.comparing(Plan::getPlanStatus)
                        .thenComparing(Comparator.comparing(Plan::getStartDate).reversed()))
                .map(p -> {
                    if (p instanceof PlanTerm) {
                        return new PlanListVO((PlanTerm) p);
                    } else {
                        return new PlanListVO((PlanRegular) p);
                    }
                }).collect(Collectors.toList());
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


    public void updateTerm_del(PlanTermUpdateForm form, LocalDate newEndDate, LocalDate endDate, Long planId) {
        /*to-do 조회 -> to-do와 연결된 tddd 조회 -> tdd와 연결된 comment 삭제 -> tdd 삭제*/

        List<Todo> todos = todoService.getTodoByPlanId(planId);
        for (Todo td : todos) {
            Iterator<LocalDate> iterator = Stream.iterate(newEndDate.plusDays(1), d -> d.plusDays(1)).limit(ChronoUnit.DAYS.between(newEndDate, endDate)).iterator();
            while (iterator.hasNext()) {
                LocalDate dk = iterator.next();
//                System.out.println(dk);
                List<TodoDate> tddr_list = todoDateService.getTodoDateRep_ByTodoAndDate(td, dk);
                for (TodoDate tdd : tddr_list) {
                    todoDateService.delete(tdd.getId());
                }
            }
        }

        /*plan과 연결된 tddr 조회 -> 삭제*/
        Iterator<LocalDate> iterator = Stream.iterate(newEndDate.plusDays(1), d -> d.plusDays(1)).limit(ChronoUnit.DAYS.between(newEndDate, endDate)).iterator();
        while (iterator.hasNext()) {
            LocalDate dk = iterator.next();
            List<TodoDateDaily> tddd_list = todoDateService.getTodoDateDaily_ByPlanIdAndDate(planId, dk);
            for (TodoDateDaily tddd : tddd_list) {
                todoDateService.delete(tddd.getId());
            }
        }

        updateTerm(form, planId);
    }


    public void updateTerm_add(PlanTermUpdateForm form, LocalDate newEndDate, LocalDate endDate, Long planId) {
        List<Todo> todos = todoService.getTodoByPlanId(planId);
        for (Todo td : todos) {
            todoDateService.todoDateInitiate(endDate.plusDays(1), newEndDate, td);
        }
        updateTerm(form, planId);
    }

    /*삭제*/
    public void delete(Long planId) {
        /*To-do 삭제*/
        List<Todo> todo_list = todoRepository.getTodoByPlanId(planId);
        todo_list.forEach(todo -> todoService.delete(todo.getId()));

        /*TodoDate(Daily) 삭제*/
        todoDateService.deleteDailyByPlanId(planId);

        /*plan 삭제*/
        planRepository.delete(planId);
    }


    /*테스트용 임시 함수*/
    public List<Plan> findOneByTitle(String title) {
        return planRepository.findOneByTitle(title);
    }
}

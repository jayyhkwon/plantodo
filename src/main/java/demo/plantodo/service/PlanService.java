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
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanService {
    private final TodoService todoService;
    private final PlanRepository planRepository;

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
        planRepository.remove(plan);
    }

}

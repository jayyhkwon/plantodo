package demo.plantodo.service;

import demo.plantodo.domain.*;
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

    /*PlanRepository 연계 메서드*/
    public void saveRegular(PlanRegular planRegular) {
        planRepository.saveRegular(planRegular);
    }
    public void saveTerm(PlanTerm planTerm) {
        planRepository.saveTerm(planTerm);
    }

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

    public void updateStatus(Long planId) {
        planRepository.updateStatus(planId);
    }

    public void planDelete(Plan plan) {
        planRepository.remove(plan);
    }

    /*business logic*/

}

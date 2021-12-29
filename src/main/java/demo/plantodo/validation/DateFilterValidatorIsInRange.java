package demo.plantodo.validation;

import demo.plantodo.VO.FilteredPlanVO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class DateFilterValidatorIsInRange implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        FilteredPlanVO filteredPlanVO = (FilteredPlanVO) target;
        LocalDate searchStart = filteredPlanVO.getSearchStart();
        LocalDate searchEnd = filteredPlanVO.getSearchEnd();
        LocalDate planStart = filteredPlanVO.getPlanStart();
        LocalDate planEnd = filteredPlanVO.getPlanEnd();

        if (searchStart.isBefore(planStart) || searchStart.isAfter(planEnd)) {
            errors.rejectValue("startDate", "range", new Object[]{planStart}, null);
        }
        if (searchEnd.isBefore(planStart) || searchEnd.isAfter(planEnd)) {
            errors.rejectValue("endDate", "range", new Object[]{planEnd}, null);
        }
    }
}

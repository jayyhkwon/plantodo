package demo.plantodo.validation;

import demo.plantodo.form.PlanRegularRegisterForm;
import demo.plantodo.form.PlanTermRegisterForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class PlanTermRegisterValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        PlanTermRegisterForm registerForm = (PlanTermRegisterForm) target;
        LocalDate today = LocalDate.now();
        LocalDate startDate = registerForm.getStartDate();
        LocalDate endDate = registerForm.getEndDate();
        if (startDate.isBefore(today)) {
            errors.rejectValue("startDate", "invalid");
        }
        if (endDate.isBefore(startDate)) {
            errors.rejectValue("endDate", "invalid");
        }
    }
}

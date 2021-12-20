package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.form.TodoRegisterForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final HomeController homeController;

    private final TodoService todoService;

    @GetMapping("/register")
    public String createRegisterForm(HttpServletRequest request, Model model) {
        Long memberId = memberRepository.getMemberId(request);

        List<Plan> plans = planRepository.findAllPlan(memberId);

        model.addAttribute("plans", plans);
        model.addAttribute("todoRegisterForm", new TodoRegisterForm());
        return "todo/register-form";
    }

    @PostMapping("/register")
    public String todoRegister(@ModelAttribute TodoRegisterForm todoRegisterForm,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               Model model) {

        int repOption = todoRegisterForm.getRepOption();
        List<String> repValue = todoRegisterForm.getRepValue();
        if ((repOption == 1 && repValue == null) || (repOption == 2 && repValue == null)) {
            Long memberId = memberRepository.getMemberId(request);
            List<Plan> plans = planRepository.findAllPlan(memberId);
            model.addAttribute("plans", plans);
            bindingResult.addError(new FieldError("todoRegisterForm", "repValue", "옵션을 추가해야 합니다."));
        }
        if (bindingResult.hasErrors()) {
            return "todo/register-form";
        }

        Long memberId = memberRepository.getMemberId(request);
        Member member = memberRepository.getMemberById(memberId).get(0);

        Plan plan = planRepository.findOne(todoRegisterForm.getPlanId());

        Todo todo = new Todo(member, plan, todoRegisterForm.getTitle(), repOption, repValue);
        todoService.todoSave(todo);

        /*TodoDate 만들기*/
        /*startDate, endDate 정의*/
        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = LocalDate.now();
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            endDate = planTerm.getEndDate();
        }

        /*todoDate 만들기*/
        todoService.todoDateInitiate(startDate, endDate, todo);
        return "redirect:/home";
    }

}

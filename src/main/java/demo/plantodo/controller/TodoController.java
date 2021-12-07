package demo.plantodo.controller;

import demo.plantodo.Service.TodoService;
import demo.plantodo.domain.*;
import demo.plantodo.form.TodoRegisterForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
                               Model model){

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
        /*로그인 검증 (이후 서블릿 필터로 대체)*/
        HttpSession session = request.getSession();
        Long memberId = (Long) session.getAttribute("memberId");
        Member member = memberRepository.getMemberById(memberId).get(0);

        Long planId = todoRegisterForm.getPlanId();
        Plan plan = planRepository.findOne(planId);
        Todo todo = new Todo(member, plan, TodoStatus.UNCHECKED, todoRegisterForm.getTitle(), repOption, repValue);
        todoService.todoSave(todo);
        /*메인 화면 세팅*/
        homeController.beforeHome(model);
        return "main-home";
    }

}

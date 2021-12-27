package demo.plantodo.controller;

import demo.plantodo.DTO.TodoButtonDTO;
import demo.plantodo.domain.*;
import demo.plantodo.form.TodoRegisterForm;
import demo.plantodo.form.TodoUpdateForm;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.PlanService;
import demo.plantodo.service.TodoDateService;
import demo.plantodo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {
    private final PlanService planService;
    private final MemberService memberService;
    private final TodoDateService todoDateService;

    private final TodoService todoService;

    @GetMapping("/register")
    public String createRegisterForm(HttpServletRequest request, Model model) {
        Long memberId = memberService.getMemberId(request);

        List<Plan> plans = planService.findAllPlan(memberId);

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
            Long memberId = memberService.getMemberId(request);
            List<Plan> plans = planService.findAllPlan(memberId);
            model.addAttribute("plans", plans);
            bindingResult.addError(new FieldError("todoRegisterForm", "repValue", "옵션을 추가해야 합니다."));
        }
        if (bindingResult.hasErrors()) {
            return "todo/register-form";
        }

        Long memberId = memberService.getMemberId(request);
        Member member = memberService.findOne(memberId);

        Plan plan = planService.findOne(todoRegisterForm.getPlanId());

        Todo todo = new Todo(member, plan, todoRegisterForm.getTitle(), repOption, repValue);
        todoService.save(todo);

        /*TodoDate 만들기*/
        /*startDate, endDate 정의*/
        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = LocalDate.now();
        if (plan instanceof PlanTerm) {
            PlanTerm planTerm = (PlanTerm) plan;
            endDate = planTerm.getEndDate();
        }

        /*todoDate 만들기*/
        todoDateService.todoDateInitiate(startDate, endDate, todo);
        return "redirect:/home";
    }

    /*to-do 삭제/수정 버튼 fragment 가져오기*/
    @GetMapping("/block")
    public String getTodoButtonBlock(@RequestParam Long planId,
                                     @RequestParam Long todoId,
                                     Model model) {
        TodoButtonDTO todoButtonDTO = new TodoButtonDTO(planId, todoId);
        model.addAttribute("todoButtonDTO", todoButtonDTO);
        return "fragments/todo-button-block :: todoButtonBlock";
    }

    /*to-do 삭제*/
    @DeleteMapping
    public RedirectView deleteTodo(@RequestParam Long planId,
                                   @RequestParam Long todoId,
                                   RedirectView redirectView) {

        todoService.delete(todoId);
        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }


    /*to-do 수정*/
    // 수정 폼 만들기
    @GetMapping("/todo")
    public String createUpdateTodoForm(@RequestParam Long planId,
                                       @RequestParam Long todoId,
                                       Model model) {

        Todo selectedTodo = todoService.findOne(todoId);
        TodoUpdateForm todoUpdateForm = new TodoUpdateForm(planId, todoId, selectedTodo.getTitle(), selectedTodo.getRepOption(), selectedTodo.getRepValue());
        model.addAttribute("todoUpdateForm", todoUpdateForm);
        return "fragments/todo-update-form-block :: todoUpdateBlock";
    }

    // 수정
    @PutMapping
    public RedirectView updateTodo(@RequestParam Long planId,
                                   @RequestParam Long todoId,
                                   @RequestParam String title,
                                   @RequestParam int repOption,
                                   @RequestParam List<String> repValue,
                                   RedirectView redirectView) {
        TodoUpdateForm todoUpdateForm = new TodoUpdateForm(planId, todoId, title, repOption, repValue);
        Plan plan = planService.findOne(planId);
        todoService.update(todoUpdateForm, todoId, plan);
        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }
}

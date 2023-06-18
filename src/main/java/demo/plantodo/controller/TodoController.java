package demo.plantodo.controller;

import demo.plantodo.VO.TodoButtonVO;
import demo.plantodo.VO.PlanSimpleVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.TodoRegisterForm;
import demo.plantodo.form.TodoUpdateForm;
import demo.plantodo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final CommonService commonService;
    private final PlanService planService;
    private final MemberService memberService;
    private final TodoDateService todoDateService;

    private final TodoService todoService;

    @GetMapping("/register")
    public String createRegisterForm(HttpServletRequest request, Model model) {
        Long memberId = commonService.getMemberId(request);
        List<PlanSimpleVO> plans = planService.findAllPlanForPlanRegister(memberId);

        model.addAttribute("plans", plans);
        model.addAttribute("todoRegisterForm", new TodoRegisterForm());
        return "todo/register-form";
    }

    @GetMapping("/emptyRepOptForm")
    public String getEmptyRepOptForm(@RequestParam Long newRepOpt) {
        if (newRepOpt == 0) return "fragments/todo-repOption-register :: repOption0";
        else if (newRepOpt == 1) return "fragments/todo-repOption-register :: repOption1";
        else return "fragments/todo-repOption-register :: repOption2";
    }

    @PostMapping("/register")
    public String todoRegister(@ModelAttribute TodoRegisterForm todoRegisterForm,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               Model model) {
        /*유효성 검증*/
        if (todoRegisterForm.getTitle() == null || todoRegisterForm.getTitle().equals("")) {
            bindingResult.addError(new FieldError("todoRegisterForm", "title", "타이틀을 추가해 주세요."));
        }

        Long memberId = commonService.getMemberId(request);
        List<PlanSimpleVO> plans = planService.findAllPlanForPlanRegister(memberId);

        int repOption = todoRegisterForm.getRepOption();
        List<String> repValue = todoRegisterForm.getRepValue();
        if ((repOption == 1 && repValue == null) || (repOption == 1 && repValue.isEmpty()) || (repOption == 2 && repValue == null) || (repOption == 2 && repValue.isEmpty())) {
            model.addAttribute("plans", plans);
            bindingResult.addError(new FieldError("todoRegisterForm", "repValue", "옵션을 추가해야 합니다."));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("plans", plans);
            model.addAttribute("todoRegisterForm", todoRegisterForm);
            return "todo/register-form";
        }

        Member member = memberService.findOne(memberId);
        Plan plan = planService.findOne(todoRegisterForm.getPlanId());

        if (repValue == null) {
            repValue = new ArrayList<>();
        }

        if (repValue.isEmpty()) {
            repValue.add("");
        }

        Todo todo = new Todo(member, plan, todoRegisterForm.getTitle(), repOption, repValue);
        todoService.save(plan, todo);
        return "redirect:/home";
    }

    /*to-do 삭제/수정 버튼 fragment 가져오기*/
    @GetMapping("/block")
    public String getTodoButtonBlock(@RequestParam Long planId,
                                     @RequestParam Long todoId,
                                     Model model) {
        TodoButtonVO todoButtonVO = new TodoButtonVO(planId, todoId);
        model.addAttribute("todoButtonDTO", todoButtonVO);
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
        TodoUpdateForm todoUpdateForm = todoService.getTodoUpdateForm(planId, todoId);
        model.addAttribute("todoUpdateForm", todoUpdateForm);
        return "fragments/todo-update-form-block :: todoUpdateBlock";
    }

    // 수정
    @GetMapping("/repOptForm")
    public String getRepOptForm(@RequestParam Long planId,
                                   @RequestParam Long todoId,
                                   @RequestParam Long newRepOpt,
                                   Model model) {
        TodoUpdateForm todoUpdateForm = todoService.getTodoUpdateForm(planId, todoId);
        model.addAttribute("todoUpdateForm", todoUpdateForm);
        if (newRepOpt == 0) return "fragments/todo-repOption-edit :: repOption0";
        else if (newRepOpt == 1) return "fragments/todo-repOption-edit :: repOption1";
        else return "fragments/todo-repOption-edit :: repOption2";
    }

    @PutMapping
    public RedirectView updateTodo(@RequestParam Long planId,
                                   @RequestParam Long todoId,
                                   @RequestParam String title,
                                   @RequestParam int repOption,
                                   @RequestParam List<String> repValue,
                                   RedirectView redirectView) {
        if (repValue.isEmpty()) {
            repValue.add("");
        }
        TodoUpdateForm todoUpdateForm = new TodoUpdateForm(planId, todoId, title, repOption, repValue);
        Plan plan = planService.findOne(planId);
        todoService.update(todoUpdateForm, todoId, plan);
        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }
}

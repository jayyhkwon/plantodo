package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.form.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanRepository planService;
    private final MemberRepository memberRepository;
    private final demo.plantodo.service.TodoService todoService;

    /*등록 - regular*/
    @GetMapping("/type")
    public String createSelectForm() {
        return "plan/plan-type";
    }

    @GetMapping("/regular")
    public String createRegularForm(Model model) {
        model.addAttribute("planRegularRegisterForm", new PlanRegularRegisterForm());
        return "plan/register-regular";
    }

    @PostMapping("/regular")
    public String planRegisterRegular(@ModelAttribute("planRegularRegisterForm") PlanRegularRegisterForm planRegularRegisterForm,
                                      HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        LocalDate startDate = LocalDate.now();
        PlanRegular planRegular = new PlanRegular(findMember, PlanStatus.NOW, startDate, planRegularRegisterForm.getTitle());
        planService.saveRegular(planRegular);
        return "redirect:/home";
    }

    /*등록 - term*/
    @GetMapping("/term")
    public String createTermForm(Model model) {
        model.addAttribute("planTermRegisterForm", new PlanTermRegisterForm());
        return "plan/register-term";
    }

    @PostMapping("/term")
    public String planRegisterTerm(@ModelAttribute("planTermRegisterForm") PlanTermRegisterForm planTermRegisterForm,
                             HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        PlanTerm planTerm = new PlanTerm(findMember, PlanStatus.NOW, planTermRegisterForm.getStartDate(), planTermRegisterForm.getTitle(), planTermRegisterForm.getEndDate());
        planService.saveTerm(planTerm);
        return "redirect:/home";
    }

    /*목록 조회*/
    @GetMapping("/plans")
    public String plans(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<Plan> plans = planService.findAllPlan(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list";
    }

    /*상세조회*/
    @GetMapping("/{planId}")
    public String plan(@PathVariable Long planId, Model model) {
        Plan selectedPlan = planService.findOne(planId);
        LocalDate startDate = selectedPlan.getStartDate();
        LocalDate endDate = LocalDate.now();

        LinkedHashMap<LocalDate, List<TodoDate>> allTodoDatesByDate = allTodosInTerm(selectedPlan, null, null);
        List<Todo> todosByPlanId = todoService.getTodoByPlanId(planId);
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("allToDatesByDate", allTodoDatesByDate);
        model.addAttribute("todosByPlanId", todosByPlanId);
        model.addAttribute("dateSearchForm", new DateSearchForm());
        return "plan/plan-detail";
    }

    /*to-do 삭제/수정 버튼 fragment 가져오기*/
    @GetMapping("/todo/block")
    public String getTodoButtonBlock(@RequestParam Long planId,
                                     @RequestParam Long todoId,
                                     Model model) {
        TodoButtonDTO todoButtonDTO = new TodoButtonDTO(planId, todoId);
        model.addAttribute("todoButtonDTO", todoButtonDTO);
        return "fragments/todo-button-block :: todoButtonBlock";
    }

    /*to-do 삭제*/
    @DeleteMapping("/todo")
    public RedirectView deleteTodo(@ModelAttribute TodoButtonDTO todoButtonDTO, RedirectView redirectView) {
        Long todoId = todoButtonDTO.getTodoId();
        Long planId = todoButtonDTO.getPlanId();

        todoService.deleteTodo(todoId);
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

        Todo selectedTodo = todoService.findOneTodo(todoId);
        TodoUpdateForm todoUpdateForm = new TodoUpdateForm(planId, todoId, selectedTodo.getTitle(), selectedTodo.getRepOption(), selectedTodo.getRepValue());
        model.addAttribute("todoUpdateForm", todoUpdateForm);
        return "fragments/todo-update-form-block :: todoUpdateBlock";
    }

    // 수정
    @PutMapping("/todo")
    public RedirectView updateTodo(@RequestParam Long planId,
                                   @RequestParam Long todoId,
                                   @RequestParam String title,
                                   @RequestParam int repOption,
                                   @RequestParam List<String> repValue,
                                   RedirectView redirectView) {
        TodoUpdateForm todoUpdateForm = new TodoUpdateForm(planId, todoId, title, repOption, repValue);
        Plan plan = planService.findOne(planId);
        todoService.updateTodo(todoUpdateForm, todoId, plan);
        String redirectURI = "/plan/" + planId;
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(redirectURI);
        return redirectView;
    }

    /*일자별 필터*/
    @PostMapping("/{planId}/filtering")
    public String filteredPlan(@PathVariable Long planId,
                               @ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm,
                               BindingResult bindingResult,
                               Model model) {
        String viewURI = "plan/plan-detail";

        Plan selectedPlan = planService.findOne(planId);
        LocalDate searchStart = dateSearchForm.getStartDate();
        LocalDate searchEnd = dateSearchForm.getEndDate();
        LocalDate planStart = selectedPlan.getStartDate();
        LocalDate planEnd = LocalDate.now();
        if (selectedPlan.getDtype().equals("Term")) {
            PlanTerm planTerm = (PlanTerm) selectedPlan;
            planEnd = planTerm.getEndDate();
        }

        if (searchStart.isBefore(planStart)) {
            String errMsg = "시작 날짜는 " + planStart + " 이후여야 합니다.";
            bindingResult.addError(new FieldError("dateSearchForm", "startDate", errMsg));
        }
        if (searchEnd.isAfter(planEnd)) {
            String errMsg = "종료 날짜는 " + planEnd + " 이전이어야 합니다.";
            bindingResult.addError(new FieldError("dateSearchForm", "endDate", errMsg));
        }
        if (bindingResult.hasErrors()) {
            return viewURI;
        }
        LinkedHashMap all = allTodosInTerm(selectedPlan, searchStart, searchEnd);
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("allTodosByDate", all);
        model.addAttribute("dateSearchForm", dateSearchForm);
        return viewURI;
    }

    /*플랜 삭제*/
    @DeleteMapping("/{planId}")
    public String planDelete(@PathVariable Long planId) {
        Plan plan = planService.findOne(planId);
        planService.remove(plan);
        return "redirect:/plan/plans";
    }

    /*플랜 변경 - 스테이터스 변경 (변경 감지 사용)*/
    @PutMapping("/{planId}/switching")
    public RedirectView planFinish(@PathVariable Long planId, RedirectView redirectView) {
        String uri = "/plan/" + planId.toString();
        planService.updateStatus(planId);
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);
        redirectView.setUrl(uri);
        return redirectView;
    }

    /*플랜 변경 - 내용 변경*/
    // 타입 결정
    @GetMapping("/type/{planId}")
    public String planTypeDefine(@PathVariable Long planId, Model model) {
        Plan plan = planService.findOne(planId);
        if (plan instanceof PlanTerm) {
            return "redirect:/plan/term/" + planId.toString();
        } else {
            return "redirect:/plan/regular/" + planId.toString();
        }
    }

    // form 생성
    @GetMapping("/regular/{planId}")
    public String planRegularUpdateForm(@PathVariable Long planId, Model model) {
        Plan plan = planService.findOne(planId);
        PlanRegularUpdateForm planRegularUpdateForm = new PlanRegularUpdateForm();
        planRegularUpdateForm.setTitle(plan.getTitle());
        model.addAttribute("planId", planId);
        model.addAttribute("planRegularUpdateForm", planRegularUpdateForm);
        return "plan/update-regular";
    }

    @GetMapping("/term/{planId}")
    public String planTermUpdateForm(@PathVariable Long planId, Model model) {
        PlanTerm plan = (PlanTerm) planService.findOne(planId);
        PlanTermUpdateForm planTermUpdateForm = new PlanTermUpdateForm();
        planTermUpdateForm.setTitle(plan.getTitle());
        planTermUpdateForm.setStartDate(plan.getStartDate());
        planTermUpdateForm.setEndDate(plan.getEndDate());
        model.addAttribute("planId", planId);
        model.addAttribute("planTermUpdateForm", planTermUpdateForm);
        return "plan/update-term";
    }

    // 내용 변경 (변경 감지)
    @PostMapping("/regular/{planId}")
    public String planRegularUpdate(@ModelAttribute PlanRegularUpdateForm planRegularUpdateForm,
                                    @PathVariable Long planId) {
        planService.updateRegular(planRegularUpdateForm, planId);
        return "redirect:/plan/" + planId.toString();
    }


    @PostMapping("/term/{planId}")
    public String planTermUpdate(@ModelAttribute PlanTermUpdateForm planTermUpdateForm,
                                 @PathVariable Long planId) {
        planService.updateTerm(planTermUpdateForm, planId);
        return "redirect:/plan/" + planId.toString();
    }

    @PostMapping("/todoDate/switching")
    public String switchStatus(@RequestParam Long planId,
                               @RequestParam Long todoDateId) {
        todoService.switchStatus(todoDateId);

        String redirectURI = "redirect:/plan/" + planId;
        return redirectURI;
    }

    /*기타 비즈니스 로직*/
    public LinkedHashMap<LocalDate, List<TodoDate>> allTodosInTerm(Plan plan, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        if (startDate==null && endDate==null) {
            startDate = plan.getStartDate();
            endDate = LocalDate.now();
            if (plan.getDtype().equals("Term")) {
                PlanTerm planTerm = (PlanTerm) plan;
                endDate = planTerm.getEndDate();
            }
        }
        int days = Period.between(startDate, endDate).getDays();

        LinkedHashMap<LocalDate, List<TodoDate>> allTodosByDate = new LinkedHashMap();

        for (int i = 0; i < days + 1; i++) {
            LocalDate date = startDate.plusDays(i);
            List<TodoDate> todoDateList = todoService.getTodoDateByDateAndPlan(plan, date);

            if (!todoDateList.isEmpty()) {
                allTodosByDate.put(date, todoDateList);
            }
        }
        return allTodosByDate;
    }
}

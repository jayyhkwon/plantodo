package demo.plantodo.controller;

import demo.plantodo.DTO.TodoButtonDTO;
import demo.plantodo.VO.FilteredPlanVO;
import demo.plantodo.domain.*;
import demo.plantodo.form.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.service.*;
import demo.plantodo.validation.DateFilterValidatorIsInRange;
import demo.plantodo.validation.DateFilterValidatorIsNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;
    private final TodoDateService todoDateService;
    private final MemberService memberService;
    private final TodoService todoService;
    private final DateFilterValidatorIsInRange isInRangeValidator;

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
        Long memberId = memberService.getMemberId(request);
        Member findMember = memberService.findOne(memberId);
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
        Long memberId = memberService.getMemberId(request);
        Member findMember = memberService.findOne(memberId);
        PlanTerm planTerm = new PlanTerm(findMember, PlanStatus.NOW, planTermRegisterForm.getStartDate(), planTermRegisterForm.getTitle(), planTermRegisterForm.getEndDate());
        planService.saveTerm(planTerm);
        return "redirect:/home";
    }

    /*목록 조회*/
    @GetMapping("/plans")
    public String plans(Model model, HttpServletRequest request) {
        Long memberId = memberService.getMemberId(request);
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

        LinkedHashMap<LocalDate, List<TodoDate>> allTodoDatesByDate = todoDateService.allTodoDatesInTerm(selectedPlan, null, null);
        List<Todo> todosByPlanId = todoService.getTodoByPlanId(planId);
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("allToDatesByDate", allTodoDatesByDate);
        model.addAttribute("todosByPlanId", todosByPlanId);
        model.addAttribute("dateSearchForm", new DateSearchForm());
        return "plan/plan-detail";
    }


    /*일자별 필터*/
    @PostMapping("/filtering")
    public String filteredPlan(@Validated @ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm,
                               BindingResult bindingResult,
                               Model model) {

        String viewURI = "plan/plan-detail";
        Long planId = dateSearchForm.getPlanId();
        Plan selectedPlan = planService.findOne(planId);
        LocalDate searchStart = dateSearchForm.getStartDate();
        LocalDate searchEnd = dateSearchForm.getEndDate();
        LocalDate planStart = selectedPlan.getStartDate();
        LocalDate planEnd = LocalDate.now();
        if (selectedPlan.getDtype().equals("Term")) {
            PlanTerm planTerm = (PlanTerm) selectedPlan;
            planEnd = planTerm.getEndDate();
        }
        List<Todo> todosByPlanId = todoService.getTodoByPlanId(planId);

        /*validation - is null*/
        FilteredPlanVO filteredPlanVO = new FilteredPlanVO(searchStart, searchEnd, planStart, planEnd);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            LinkedHashMap<LocalDate, List<TodoDate>> all = todoDateService.allTodoDatesInTerm(selectedPlan, null, null);
            setAttributesForPast(dateSearchForm, model, selectedPlan, all, todosByPlanId);
            return viewURI;
        }

        /*validation - is in range*/
        isInRangeValidator.validate(filteredPlanVO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            LinkedHashMap<LocalDate, List<TodoDate>> all = todoDateService.allTodoDatesInTerm(selectedPlan, null, null);
            setAttributesForPast(dateSearchForm, model, selectedPlan, all, todosByPlanId);
            return viewURI;
        }
        LinkedHashMap<LocalDate, List<TodoDate>> all = todoDateService.allTodoDatesInTerm(selectedPlan, searchStart, searchEnd);
        setAttributesForPast(dateSearchForm, model, selectedPlan, all, todosByPlanId);
        return viewURI;
    }

    private void setAttributesForPast(@ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm, Model model, Plan selectedPlan, LinkedHashMap<LocalDate, List<TodoDate>> all, List<Todo> todosByPlanId) {
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("todosByPlanId", todosByPlanId);
        model.addAttribute("allToDatesByDate", all);
        model.addAttribute("dateSearchForm", dateSearchForm);
    }


    /*플랜 삭제*/
    @DeleteMapping
    public String planDelete(@RequestParam Long planId) {
        System.out.println("planId = " + planId);
        Plan plan = planService.findOne(planId);
        planService.delete(plan);
        return "redirect:/plan/plans";
    }

    /*플랜 변경 - 스테이터스 변경 (변경 감지 사용)*/
    @PutMapping("/switching")
    public RedirectView planFinish(@RequestParam Long planId, RedirectView redirectView) {
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

}

package demo.plantodo.controller;

import demo.plantodo.VO.*;
import demo.plantodo.domain.*;
import demo.plantodo.form.*;
import demo.plantodo.service.*;
import demo.plantodo.validation.DateFilterValidatorIsInRange;
import demo.plantodo.validation.PlanTermRegisterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {

    private final PlanService planService;
    private final TodoDateService todoDateService;
    private final MemberService memberService;
    private final TodoService todoService;
    private final DateFilterValidatorIsInRange isInRangeValidator;
    private final PlanTermRegisterValidator planTermRegisterValidator;
    private final AuthService authService;

    /*등록 - regular*/
    @GetMapping("/regular")
    public String createRegularForm(Model model) {
        model.addAttribute("planRegularRegisterForm", new PlanRegularRegisterForm());
        return "plan/register-regular";
    }

    @PostMapping("/regular")
    public String planRegisterRegular(@Validated @ModelAttribute("planRegularRegisterForm") PlanRegularRegisterForm form,
                                      BindingResult bindingResult,
                                      Model model,
                                      @CookieValue(name = "AUTH") String authKey) {

        if (bindingResult.hasErrors()) {
            // model.addAttribute("planRegularRegisterForm", form);
            return "plan/register-regular";
        }

        Long memberId = authService.getMemberIdByKey(authKey);
        Member findMember = memberService.findOne(memberId);
        LocalDate startDate = LocalDate.now();
        PlanRegular planRegular = new PlanRegular(findMember, PlanStatus.NOW, startDate, form.getTitle());
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
    public String planRegisterTerm(@Validated @ModelAttribute("planTermRegisterForm") PlanTermRegisterForm form,
                             BindingResult bindingResult,
                             Model model,
                             @CookieValue(name = "AUTH") String authKey) {
        /*null 검증*/
        if (bindingResult.hasErrors()) {
            // model.addAttribute("planTermRegisterForm", form);
            return "plan/register-term";
        }
        /*startDate가 오늘 이전인 경우 / endDate가 startDate 이전인 경우 검증*/
        planTermRegisterValidator.validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "plan/register-term";
        }
        Long memberId = authService.getMemberIdByKey(authKey);
        Member findMember = memberService.findOne(memberId);

        planService.saveTerm(findMember, form);
        return "redirect:/home";
    }

    /*목록 조회*/
    @GetMapping("/plans")
    public String plans(Model model, @CookieValue(name = "AUTH") String authKey, HttpServletResponse response) {
        Long memberId = authService.getMemberIdByKey(authKey);
        List<PlanListVO> plans = planService.findAllPlan_withCompPercent(memberId);
        model.addAttribute("plans", plans);
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        return "plan/plan-list";
    }

    /*상세조회*/
    @GetMapping("/{planId}")
    public String plan(@PathVariable Long planId, Model model) {
        Plan plan = planService.findOne(planId);

        LocalDate endDate = LocalDate.now();
        LinkedHashMap<LocalDate, List<TodoDateVO>> allTodoDatesByDate = todoDateService.allTodoDatesInTerm(plan, null, null);
        List<TodoDetailVO> todos = todoService.getTodoVOByPlanId(planId);
        model.addAttribute(  "plan", new PlanDetailVO(plan, endDate));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("allToDatesByDate", allTodoDatesByDate);
        model.addAttribute("todosByPlanId", todos);
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

        List<TodoDetailVO> todos = todoService.getTodoVOByPlanId(planId);

        /*validation - is null*/
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            LinkedHashMap<LocalDate, List<TodoDateVO>> all = todoDateService.allTodoDatesInTerm(selectedPlan, null, null);
            setAttributesForPast(dateSearchForm, model, new PlanDetailVO(selectedPlan, planEnd), all, todos);
            return viewURI;
        }

        /*validation - format (range)*/
        FilteredPlanVO filteredPlanVO = new FilteredPlanVO(searchStart, searchEnd, planStart, planEnd);
        isInRangeValidator.validate(filteredPlanVO, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            LinkedHashMap<LocalDate, List<TodoDateVO>> all = todoDateService.allTodoDatesInTerm(selectedPlan, null, null);
            setAttributesForPast(dateSearchForm, model, new PlanDetailVO(selectedPlan, planEnd), all, todos);
            return viewURI;
        }
        LinkedHashMap<LocalDate, List<TodoDateVO>> all = todoDateService.allTodoDatesInTerm(selectedPlan, searchStart, searchEnd);
        setAttributesForPast(dateSearchForm, model, new PlanDetailVO(selectedPlan, planEnd), all, todos);
        return viewURI;
    }

    private void setAttributesForPast(@ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm, Model model, PlanDetailVO selectedPlan, LinkedHashMap<LocalDate, List<TodoDateVO>> all, List<TodoDetailVO> todosByPlanId) {
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("todosByPlanId", todosByPlanId);
        model.addAttribute("allToDatesByDate", all);
        model.addAttribute("dateSearchForm", dateSearchForm);
    }


    /*플랜 삭제*/
    @DeleteMapping
    public String planDelete(@RequestParam Long planId) {
        planService.delete(planId);
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
        planTermUpdateForm.setEndTime(plan.getEndTime().toString());
        model.addAttribute("planId", planId);
        model.addAttribute("planTermUpdateForm", planTermUpdateForm);
        return "plan/update-term";
    }

    // 내용 변경 (변경 감지)
    @PostMapping("/regular/{planId}")
    public String planRegularUpdate(@ModelAttribute PlanRegularUpdateForm planRegularUpdateForm,
                                    @PathVariable Long planId,
                                    BindingResult bindingResult,
                                    Model model) {

        if (planRegularUpdateForm.getTitle() == null || planRegularUpdateForm.getTitle().equals("")) {
            bindingResult.addError(new FieldError("planRegularUpdateForm", "title", "타이틀을 입력해주세요."));
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("planRegularUpdateForm", planRegularUpdateForm);
            return "plan/update-regular";
        }

        planService.updateRegular(planRegularUpdateForm, planId);
        return "redirect:/plan/" + planId.toString();
    }

    @PostMapping("/term/{planId}")
    public String planTermUpdate(@ModelAttribute PlanTermUpdateForm planTermUpdateForm,
                                 @PathVariable Long planId,
                                 BindingResult bindingResult,
                                 Model model) {

        if (planTermUpdateForm.getTitle() == null || planTermUpdateForm.getTitle().equals("")) {
            bindingResult.addError(new FieldError("planTermUpdateForm", "title", "타이틀을 입력해주세요."));
        }

        LocalDate newEndDate = planTermUpdateForm.getEndDate();
        if (newEndDate.isBefore(LocalDate.now())) {
            bindingResult.addError(new FieldError("planTermUpdateForm", "endDate", "종료일은 오늘 이후로 설정해주셔야 합니다."));
        }

        if (bindingResult.hasErrors()) {
            System.out.println("error detected!");
            model.addAttribute("planTermUpdateForm", planTermUpdateForm);
            return "plan/update-term";
        }

        PlanTerm planTerm = (PlanTerm) planService.findOne(planId);
        LocalDate endDate = planTerm.getEndDate();
        if (newEndDate.isBefore(endDate)) {
            planService.updateTerm_del(planTermUpdateForm, newEndDate, endDate, planId);
        } else if (newEndDate.isAfter(endDate)) {
            planService.updateTerm_add(planTermUpdateForm, newEndDate, endDate, planId);
        } else {
            planService.updateTerm(planTermUpdateForm, planId);
        }
        return "redirect:/plan/" + planId.toString();
    }


    /*플랜 변경 - 강조*/
    @PostMapping("/emphasizing")
    public String switchPlanEmphasis(@RequestParam Long planId,
                                     @RequestParam String pageInfo) {
        planService.switchPlanEmphasis(planId);
        if (pageInfo.equals("detail")) {
            return "redirect:/plan/" + planId.toString();
        } else {
            return "redirect:/plan/plans";
        }

    }

}

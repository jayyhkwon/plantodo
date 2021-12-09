package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.form.DateSearchForm;
import demo.plantodo.form.PlanRegularRegisterForm;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final demo.plantodo.service.TodoService todoService;

    /*등록 - regular*/
    @GetMapping("/type")
    public String createSelectForm() {
        return "plan/plan-type";
    }

    @GetMapping("/register/regular")
    public String createRegularForm(Model model) {
        model.addAttribute("planRegularRegisterForm", new PlanRegularRegisterForm());
        return "plan/register-regular";
    }

    @PostMapping("/register/regular")
    public String planRegisterRegular(@ModelAttribute("planRegularRegisterForm") PlanRegularRegisterForm planRegularRegisterForm,
                                      HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        LocalDate startDate = LocalDate.now();
        PlanRegular planRegular = new PlanRegular(findMember, PlanStatus.NOW, startDate, planRegularRegisterForm.getTitle());
        planRepository.saveRegular(planRegular);
        return "redirect:/home";
    }
    /*등록 - term*/
    @GetMapping("/register/term")
    public String createTermForm(Model model) {
        model.addAttribute("planTermRegisterForm", new PlanTermRegisterForm());
        return "plan/register-term";
    }

    @PostMapping("/register/term")
    public String planRegisterTerm(@ModelAttribute("planTermRegisterForm") PlanTermRegisterForm planTermRegisterForm,
                             HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        PlanTerm planTerm = new PlanTerm(findMember, PlanStatus.NOW, planTermRegisterForm.getStartDate(), planTermRegisterForm.getTitle(), planTermRegisterForm.getEndDate());
        planRepository.saveTerm(planTerm);
        return "redirect:/home";
    }

    /*목록 조회*/
    @GetMapping("/regular")
    public String PlansRegular(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<PlanRegular> plans = planRepository.findAllPlanRegular(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list-regular";
    }

    @GetMapping("/term")
    public String PlansTerm(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<PlanTerm> plans = planRepository.findAllPlanTerm(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list-term";
    }

    /*상세조회*/
    @GetMapping("regular/{planId}")
    public String planRegular(@PathVariable Long planId, Model model) {
        Plan selectedPlan = planRepository.findOne(planId);
        LocalDate startDate = selectedPlan.getStartDate();
        LocalDate endDate = LocalDate.now();

        int days = Period.between(startDate, endDate).getDays();

        LinkedHashMap all = allTodosByDate(selectedPlan, "regular");
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("allTodosByDate", all);
        model.addAttribute("dateSearchForm", new DateSearchForm());
        return "plan/plan-detail-regular";
    }

    @GetMapping("term/{planId}")
    public String planTerm(@PathVariable Long planId, Model model) {
        Plan selectedPlan = planRepository.findOne(planId);
        LinkedHashMap all = allTodosByDate(selectedPlan, "term");
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("allTodosByDate", all);
        model.addAttribute("dateSearchForm", new DateSearchForm());
        return "plan/plan-detail-term";
    }

//    @PostMapping("/regular/{planId}")
//    public String filteredPlan(@PathVariable Long planId,
//                               @ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm,
//                               BindingResult bindingResult,
//                               Model model) {
//        PlanRegular selectedPlan = planRepository.findOne(planId);
//        LocalDate searchStart = dateSearchForm.getStartDate();
//        LocalDate searchEnd = dateSearchForm.getEndDate();
//
//        if (searchStart.isBefore(planStart)) {
//            String errMsg = "시작 날짜는 " + planStart + " 이후여야 합니다.";
//            bindingResult.addError(new FieldError("dateSearchForm", "startDate", errMsg));
//        }
//        if (searchEnd.isAfter(planEnd)) {
//            String errMsg = "종료 날짜는 " + planEnd + " 이전이어야 합니다.";
//            bindingResult.addError(new FieldError("dateSearchForm", "endDate", errMsg));
//        }
//        if (bindingResult.hasErrors()) {
//            return "plan/plan-detail";
//        }
//        int days = Period.between(searchStart, searchEnd).getDays();
//        LinkedHashMap all = allTodosByDate(selectedPlan, searchStart, days);
//        model.addAttribute("plan", selectedPlan);
//        model.addAttribute("allTodosByDate", all);
//        model.addAttribute("dateSearchForm", dateSearchForm);
//        return "plan/plan-detail";
//    }

    private LinkedHashMap allTodosByDate(Plan plan, String type) {
        LocalDate startDate = plan.getStartDate();
        LocalDate endDate = LocalDate.now();
        if (plan.getClass().isInstance(PlanTerm.class)) {
            PlanTerm planTerm = (PlanTerm) plan;
            endDate = planTerm.getEndDate();
        }

        int days = Period.between(startDate, endDate).getDays();

        LinkedHashMap allTodosByDate = new LinkedHashMap();

        for (int i = 0; i < days + 1; i++) {
            LocalDate date = startDate.plusDays(i);
            List<Todo> todoInDate = new ArrayList<>();

            todoInDate = todoService.getTodoByDate(plan, date);

            if (!todoInDate.isEmpty()) {
                allTodosByDate.put(date, todoInDate);
            }
        }
        return allTodosByDate;
    }
}

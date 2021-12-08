package demo.plantodo.controller;

import demo.plantodo.Service.TodoService;
import demo.plantodo.converter.StringToLocalDateConverter;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final TodoService todoService;
    StringToLocalDateConverter stringToLocalDateConverter = new StringToLocalDateConverter();

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
        PlanTerm planTerm = new PlanTerm(findMember, PlanStatus.NOW, planTermRegisterForm.getStartDate(), planTermRegisterForm.getEndDate(), planTermRegisterForm.getTitle());
        planRepository.saveTerm(planTerm);
        return "redirect:/home";
    }

    @GetMapping("/regular")
    public String RegularPlans(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<PlanRegular> plans = planRepository.findAllPlanRegular(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list-regular";
    }

    @GetMapping("/term")
    public String TermPlans(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<PlanTerm> plans = planRepository.findAllPlanTerm(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list-term";
    }

//    @GetMapping("/{planId}")
//    public String plan(@PathVariable Long planId, Model model) {
//        PlanRegular selectedPlan = planRepository.findOne(planId);
//        LocalDate startDate = stringToLocalDateConverter.convert(selectedPlan.getStartDate());
//        LocalDate endDate = stringToLocalDateConverter.convert(selectedPlan.getEndDate());
//
//        System.out.println("endDate = " + endDate);
//        int days = Period.between(startDate, endDate).getDays();
//        System.out.println("days = " + days);
//
//        LinkedHashMap all = allTodosByDate(selectedPlan, startDate, days);
//        model.addAttribute("plan", selectedPlan);
//        model.addAttribute("allTodosByDate", all);
//        model.addAttribute("dateSearchForm", new DateSearchForm());
//        return "plan/plan-detail";
//
//    }

//    @PostMapping("/{planId}")
//    public String filteredPlan(@PathVariable Long planId,
//                               @ModelAttribute("dateSearchForm") DateSearchForm dateSearchForm,
//                               BindingResult bindingResult,
//                               Model model) {
//        PlanRegular selectedPlan = planRepository.findOne(planId);
//        LocalDate searchStart = dateSearchForm.getStartDate();
//        LocalDate searchEnd = dateSearchForm.getEndDate();
//        LocalDate planStart = stringToLocalDateConverter.convert(selectedPlan.getStartDate());
//        LocalDate planEnd = stringToLocalDateConverter.convert(selectedPlan.getEndDate());
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
//
//
//    private LinkedHashMap allTodosByDate(PlanRegular plan, LocalDate startDate, int days) {
//        LinkedHashMap allTodosByDate = new LinkedHashMap();
//        for (int i = 0; i < days + 1; i++) {
//            LocalDate date = startDate.plusDays(i);
//            List<Todo> todoInDate = todoService.getTodoByDateFilter(plan, date);
//            if (!todoInDate.isEmpty()) {
//                System.out.println("todoInDate = " + todoInDate);
//                allTodosByDate.put(date, todoInDate);
//            }
//        }
//        return allTodosByDate;
//    }
}

package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.form.DateSearchForm;
import demo.plantodo.form.PlanRegularRegisterForm;
import demo.plantodo.form.PlanTermRegisterForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import demo.plantodo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

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
        Plan plan = new Plan(findMember, PlanStatus.NOW, startDate, null, planRegularRegisterForm.getTitle());
        planRepository.save(plan);
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
        Plan plan = new Plan(findMember, PlanStatus.NOW, planTermRegisterForm.getStartDate(), planTermRegisterForm.getEndDate(), planTermRegisterForm.getTitle());
        planRepository.save(plan);
        return "redirect:/home";
    }

    @GetMapping
    public String plans(Model model, HttpServletRequest request) {
        Long memberId = memberRepository.getMemberId(request);
        List<Plan> plans = planRepository.findAllPlan(memberId);
        model.addAttribute("plans", plans);
        return "plan/plan-list";
    }

    @GetMapping("/{planId}")
    public String plan(@PathVariable Long planId, Model model, HttpServletRequest request) {
        Plan selectedPlan = planRepository.findOne(planId);
        LocalDate startDate = selectedPlan.getStartDate();
        LocalDate endDate = Optional.ofNullable(selectedPlan.getEndDate()).orElse(LocalDate.now());
        int days = Period.between(startDate, endDate).getDays();

        LinkedHashMap allTodosByDate = new LinkedHashMap();
        for (int i = 0; i < days+1; i++) {
            LocalDate date = startDate.plusDays(i);
            List<Todo> todoInDate = todoRepository.getTodoByPlanIdAndDate(planId, date);
            if (!todoInDate.isEmpty()) {
                allTodosByDate.put(date, todoInDate);
            }
        }
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("allTodosByDate", allTodosByDate);
        model.addAttribute("dateSearchForm", new DateSearchForm());
        return "plan/plan-detail";

    }
}

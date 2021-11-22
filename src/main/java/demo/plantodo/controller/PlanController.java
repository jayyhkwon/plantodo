package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private PlanRepository planRepository;
    private MemberRepository memberRepository;

    @GetMapping("/register")
    public String createRegisterForm(Model model) {
        model.addAttribute("planRegisterForm", new PlanRegisterForm());
        return "plan/register-form";
    }

    @PostMapping("/register")
    public void planRegister(@ModelAttribute("planRegisterForm") PlanRegisterForm planRegisterForm,
                             BindingResult bindingResult,
                             HttpServletRequest request) {
        Period period = new Period(planRegisterForm.getStartDate(), planRegisterForm.getEndDate());
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("memberId");
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        Plan plan = new Plan(findMember, PlanStatus.NOW, period, planRegisterForm.getTitle());
        planRepository.save(plan);
    }
}

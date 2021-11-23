package demo.plantodo.controller;

import demo.plantodo.domain.*;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/register")
    public String createRegisterForm(Model model) {
        model.addAttribute("planRegisterForm", new PlanRegisterForm());
        return "plan/register-form";
    }

    @PostMapping("/register")
    public String planRegister(@ModelAttribute("planRegisterForm") PlanRegisterForm planRegisterForm,
                             HttpServletRequest request) {
        HttpSession session = request.getSession();
        Long memberId = (Long) session.getAttribute("memberId");
        Member findMember = memberRepository.getMemberById(memberId).get(0);
        Plan plan = new Plan(findMember, PlanStatus.NOW, planRegisterForm.getStartDate(), planRegisterForm.getEndDate(), planRegisterForm.getTitle());
        planRepository.save(plan);
        return "/home";
    }
}

package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.form.MemberJoinForm;
import demo.plantodo.form.MemberLoginForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final HomeController homeController;
    @GetMapping(value = "/join")
    public String createJoinForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join-form";
    }

    @PostMapping(value = "/join")
    public String joinMember(@ModelAttribute("memberJoinForm") MemberJoinForm memberJoinForm,
                             BindingResult bindingResult,
                             Model model) {
        List<Member> memberByEmail = memberService.getMemberByEmail(memberJoinForm.getEmail());
        /*검증 코드*/
        if (!memberByEmail.isEmpty()) {
            bindingResult.addError(new FieldError("memberJoinForm", "email", "사용할 수 없는 이메일입니다."));
        }
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }
        Member member = new Member(memberJoinForm.getEmail(), memberJoinForm.getPassword(), memberJoinForm.getNickname());
        memberService.save(member);
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String createLoginForm(Model model) {
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "member/login-form";
    }

    @PostMapping("/login")
    public String loginMember(@ModelAttribute("memberLoginForm") MemberLoginForm memberLoginForm,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              Model model) {
        List<Member> findMember = memberService.getMemberByEmail(memberLoginForm.getEmail());

        if (findMember.isEmpty()) {
            bindingResult.addError(new FieldError("memberLoginForm", "email", "존재하지 않는 회원입니다."));
        }

        String candidate = memberLoginForm.getPassword();
        Member rightMember = findMember.get(0);

        if (!candidate.equals(rightMember.getPassword())) {
            bindingResult.addError(new FieldError("memberLoginForm", "password", "비밀번호가 틀렸습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }
        HttpSession session = request.getSession();
        session.setAttribute("memberId", rightMember.getId());
        /*로그인 세션 유지 시간 (임의 변경 가능)*/
        session.setMaxInactiveInterval(300);
        return "redirect:/home";
    }



}

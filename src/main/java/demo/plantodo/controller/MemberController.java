package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.MemberJoinForm;
import demo.plantodo.domain.MemberLoginForm;
import demo.plantodo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping(value = "/join")
    public String createJoinForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join-form";
    }

    @PostMapping(value = "/join")
    public String joinMember(@ModelAttribute("memberJoinForm") MemberJoinForm memberJoinForm,
                             BindingResult bindingResult,
                             Model model) {
        List<Member> memberByEmail = memberRepository.getMemberByEmail(memberJoinForm.getEmail());
        /*검증 코드*/
        if (!memberByEmail.isEmpty()) {
            bindingResult.addError(new FieldError("memberJoinForm", "email", "사용할 수 없는 이메일입니다."));
        }
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }
        Member member = new Member(memberJoinForm.getEmail(), memberJoinForm.getPassword(), memberJoinForm.getNickname());
        memberRepository.save(member);
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "member/login-form";
    }

    @GetMapping("/login")
    public String createLoginForm(Model model) {
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "member/login-form";
    }

    @PostMapping("/login")
    public String loginMember(@ModelAttribute("memberLoginForm") MemberLoginForm memberLoginForm,
                              BindingResult bindingResult) {
        List<Member> findMember = memberRepository.getMemberByEmail(memberLoginForm.getEmail());
        if (findMember.isEmpty()) {
            bindingResult.addError(new FieldError("memberLoginForm", "email", "존재하지 않는 회원입니다."));
        }

        String candidate = memberLoginForm.getPassword();
        Member rightMember = findMember.get(0);
        if (candidate != rightMember.getPassword()) {
            bindingResult.addError(new FieldError("memberLoginForm", "password", "비밀번호가 틀렸습니다."));
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }
        System.out.println("login success!");
        return "../home";
    }

}

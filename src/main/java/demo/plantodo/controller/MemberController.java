package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.MemberJoinForm;
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
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping(value = "/member/join")
    public String createJoinForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "/member/join-form";
    }

    @PostMapping(value = "/member/join")
    public String joinMember(@ModelAttribute MemberJoinForm memberJoinForm,
                             BindingResult bindingResult, Model model) {
        List<Member> memberByEmail = memberRepository.getMemberByEmail(memberJoinForm.getEmail());
        /*검증 코드*/
        if (!memberByEmail.isEmpty()) {
            bindingResult.addError(new FieldError("member", "email", "사용할 수 없는 이메일입니다."));
        }
        if (bindingResult.hasErrors()) {
            return "/member/join-form";
        }
        Member member = new Member(memberJoinForm.getEmail(), memberJoinForm.getPassword(), memberJoinForm.getNickname());
        memberRepository.save(member);
        return "/member/login-form";
    }
    

}

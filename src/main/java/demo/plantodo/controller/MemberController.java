package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping(value = "/member/join-form")
    public String createJoinForm() {
        return "/member/join-form";
    }

    @GetMapping(value = "/member/login-form")
    public String createLoginForm() {
        return "/member/login-form";
    }

    @PostMapping(value = "/member/join")
    public String joinMember(@ModelAttribute Member member) {
        return memberService.joinMember(member);
    }
}

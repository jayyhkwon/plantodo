package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import demo.plantodo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/member/join")
    public String createJoinForm() {
        return "/member/join-form";
    }

    @PostMapping("/member/join")
    public String joinMember(@RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("nickname") String nickname,
                             @ModelAttribute Member member, BindingResult bindingResult) {
        
        List<Member> memberByEmail = memberRepository.getMemberByEmail(email);
        /*검증 코드*/
        if (!memberByEmail.isEmpty()) {
            bindingResult.addError(new FieldError("member", "email", "사용할 수 없는 이메일입니다."));
        }
        if (bindingResult.hasErrors()) {
            return "/member/join-form";
        }
        Member newMember = new Member(email, password, nickname);
        memberRepository.save(newMember);
        return "/member/login-form";
    }
    

}

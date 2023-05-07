package demo.plantodo.controller;

import demo.plantodo.converter.StringToEnumConverterFactory;
import demo.plantodo.domain.Auth;
import demo.plantodo.domain.Member;
import demo.plantodo.domain.PermStatus;
import demo.plantodo.domain.Settings;
import demo.plantodo.form.MemberJoinForm;
import demo.plantodo.form.MemberLoginForm;
import demo.plantodo.service.AuthService;
import demo.plantodo.service.CommonService;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.SettingsService;
import demo.plantodo.validation.MemberJoinlValidator;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final SettingsService settingsService;
    private final AuthService authService;
    private final MemberJoinlValidator memberJoinlValidator;
    private final StringToEnumConverterFactory converterFactory;
    private final CommonService commonService;

    @GetMapping(value = "/join")
    public String createJoinForm(Model model) {
        model.addAttribute("memberJoinForm", new MemberJoinForm());
        return "member/join-form";
    }

    @PostMapping(value = "/join")
    public String joinMember(@Validated @ModelAttribute("memberJoinForm") MemberJoinForm memberJoinForm,
                             BindingResult bindingResult,
                             Model model) {
        /*검증 코드*/
        /*null값 검증을 통과하지 못하면 이전 페이지로 돌려보내기*/
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        /*이메일/패스워드 형식 검증*/
        memberJoinlValidator.validate(memberJoinForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        /*이미 가입이 된 이메일이면 이전 페이지로 돌려보내기*/
        List<Member> memberByEmail = memberService.getMemberByEmail(memberJoinForm.getEmail());
        if (!memberByEmail.isEmpty()) {
            bindingResult.rejectValue("email", "duplicate");
        }
        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        PermStatus status = converterFactory.getConverter(PermStatus.class).convert(memberJoinForm.getPermission());
        Settings settings = new Settings(status);
        settingsService.save(settings);

        Member member = new Member(memberJoinForm.getEmail(), memberJoinForm.getPassword(), memberJoinForm.getNickname(), settings);
        memberService.save(member);

        // auth 생성
        Long memberId = member.getId();
        authService.save(memberId);

        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "redirect:/member/login";
    }


    @GetMapping("/login")
    public String createLoginForm(Model model) {
        model.addAttribute("memberLoginForm", new MemberLoginForm());
        return "member/login-form";
    }

    @PostMapping("/login")
    public String loginMember(@Validated @ModelAttribute("memberLoginForm") MemberLoginForm memberLoginForm,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        /*null값 체크 후 에러가 발생했을 경우 이전 페이지로 돌아가기*/
        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        /*해당 이메일로 가입된 계정이 있는지 확인*/
        List<Member> findMember = memberService.getMemberByEmail(memberLoginForm.getEmail());

        if (findMember.isEmpty()) {
            bindingResult.rejectValue("email", "invalid");
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        /*패스워드가 맞는지 확인*/
        String candidate = memberLoginForm.getPassword();
        Member rightMember = findMember.get(0);

        if (!candidate.equals(rightMember.getPassword())) {
            bindingResult.rejectValue("password", "invalid");
        }

        if (bindingResult.hasErrors()) {
            return "member/login-form";
        }

        HttpSession session = request.getSession();
        session.setAttribute("nickname", rightMember.getNickname());

        /*마감 알람 on-off 여부 확인*/
        Settings settings = memberService.findOne(rightMember.getId()).getSettings();

        /*혹시 firstAccess, deadline_alarm_term 쿠키가 아직 있는 경우 삭제*/
        ResponseCookie pastFirstAccess = commonService.makeCookie("firstAccess", null, 0);
        response.setHeader("Set-Cookie", pastFirstAccess.toString());

        ResponseCookie pastDAT = commonService.makeCookie("deadline_alarm_term", null, 0);
        response.setHeader("Set-Cookie", pastDAT.toString());

        /*Auth 쿠키 생성*/
        String authKey = String.valueOf(authService.getKeyByMemberId(rightMember.getId()));
        ResponseCookie auth = commonService.makeCookie("AUTH", authKey, 1800);
        response.setHeader("Set-Cookie", auth.toString());

        if (settings.getNotification_perm().toString().equals("GRANTED") && settings.isDeadline_alarm()) {
            ResponseCookie firstAccessCookie = commonService.makeCookie("firstAccess", "1", 1800);
            response.addHeader("Set-Cookie", firstAccessCookie.toString());

            ResponseCookie datCookie = commonService.makeCookie("deadline_alarm_term", String.valueOf(settings.getDeadline_alarm_term()), 300);
            response.addHeader("Set-Cookie", datCookie.toString());
        } else {
            ResponseCookie datCookie = commonService.makeCookie("deadline_alarm_term", "-1", 1800);
            response.addHeader("Set-Cookie", datCookie.toString());
        }

        /*로그인 세션 유지 시간 (임의 변경 가능)*/
        session.setMaxInactiveInterval(1800);

        log.warn("login complete!");
        return "redirect:home";
    }

    @GetMapping("/logout")
    public String logoutMember(HttpServletResponse response) {
        ResponseCookie auth = commonService.makeCookie("AUTH", "", 0);
        response.setHeader("Set-Cookie", auth.toString());

        ResponseCookie dat = commonService.makeCookie("deadline_alarm_term", "", 0);
        response.setHeader("Set-Cookie", dat.toString());

        ResponseCookie firstAccess = commonService.makeCookie("firstAccess", "", 0);
        response.setHeader("Set-Cookie", firstAccess.toString());
        return "redirect:/";
    }


}

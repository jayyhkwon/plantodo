package demo.plantodo.controller;

import demo.plantodo.domain.Settings;
import demo.plantodo.form.SettingsUpdateForm;
import demo.plantodo.service.AuthService;
import demo.plantodo.service.CommonService;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final AuthService authService;
    private final MemberService memberService;
    private final SettingsService settingsService;

    @GetMapping
    public String createSettingsUpdateForm(Model model, @CookieValue(name = "AUTH") String authKey) {
        Long memberId = authService.getMemberIdByKey(authKey);

        Settings settings = settingsService.findOneByMemberId(memberId);
        SettingsUpdateForm form = new SettingsUpdateForm(settings.getNotification_perm(), settings.isDeadline_alarm() ? true : false, settings.getDeadline_alarm_term(), settings.getId());
        model.addAttribute("settingsUpdateForm", form);
        return "member/settings-form";
    }

    @PostMapping("/update")
    public String updateSettings(@RequestBody SettingsUpdateForm settingsUpdateForm) {
        if (settingsUpdateForm.getNotification_perm() == null) {
            log.info("A null value will be inserted to settingsUpdateForm");
        }
        settingsService.update(settingsUpdateForm.getSettings_id(), settingsUpdateForm);
        return "redirect:/settings";
    }


}

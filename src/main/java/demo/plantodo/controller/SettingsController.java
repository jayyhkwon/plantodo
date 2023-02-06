package demo.plantodo.controller;

import demo.plantodo.domain.Settings;
import demo.plantodo.form.SettingsUpdateForm;
import demo.plantodo.service.MemberService;
import demo.plantodo.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final MemberService memberService;
    private final SettingsService settingsService;

    @GetMapping
    public String createSettingsUpdateForm(Model model, HttpServletRequest request) {
        Settings settings = settingsService.findOneByMemberId(memberService.getMemberId(request));
        SettingsUpdateForm form = new SettingsUpdateForm(settings.getNotification_perm(), settings.isDeadline_alarm() ? true : false, settings.getDeadline_alarm_term(), settings.getId());
        model.addAttribute("settingsUpdateForm", form);
        return "/member/settings-form";
    }

    @PostMapping("/update")
    public String updateSettings(@RequestBody SettingsUpdateForm settingsUpdateForm) {
        settingsService.update(settingsUpdateForm.getSettings_id(), settingsUpdateForm);
        return "redirect:/settings";
    }


}

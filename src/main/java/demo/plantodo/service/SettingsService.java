package demo.plantodo.service;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.PermStatus;
import demo.plantodo.domain.Settings;
import demo.plantodo.form.MemberJoinForm;
import demo.plantodo.form.SettingsUpdateForm;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final MemberRepository memberRepository;
    private final SettingsRepository settingsRepository;

    public void save(Settings settings) {
        settingsRepository.save(settings);
    }

    public Settings findOne(Long id) {
        return settingsRepository.findOne(id);
    }

    public void remove(Settings settings) {
        settingsRepository.remove(settings);
    }

    public Settings findOneByMemberId(Long memberId) {
        return memberRepository.findOne(memberId).getSettings();
    }

    public void update(Long settingsId, SettingsUpdateForm settingsUpdateForm) {
        settingsRepository.update(settingsId, settingsUpdateForm);
    }


}

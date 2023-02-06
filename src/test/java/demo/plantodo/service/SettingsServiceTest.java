package demo.plantodo.service;

import demo.plantodo.domain.Member;
import demo.plantodo.domain.Settings;
import demo.plantodo.form.SettingsUpdateForm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SettingsServiceTest {
    @Autowired private MemberService memberService;
    @Autowired private SettingsService settingsService;

    @Test
    @DisplayName("Member를 저장하면 Settings가 저장된다.")
    public void saveTest() throws Exception {
        //given
        Settings settings = new Settings("denied");
        settingsService.save(settings);

        Member member = new Member("test@abc.co.kr", "abc123!@#", "테스트", settings);

        //when
        memberService.save(member);

        //then
        Assertions.assertThat(settingsService.findOneByMemberId(member.getId()).getId()).isEqualTo(settings.getId());
    }

//    @Test
//    @DisplayName("settingsUpdateForm으로 settings를 수정한다.")
//    public void updateTest() throws Exception {
//        //given
//        Settings settings = new Settings("denied");
//        settingsService.save(settings);
//
//        Member member = new Member("test@abc.co.kr", "abc123!@#", "테스트", settings);
//        memberService.save(member);
//
//        //when
//        SettingsUpdateForm form = new SettingsUpdateForm("granted", true, 30, settings.getId());
//        settingsService.update(member.getId(), form);
//
//        //then
//        Assertions.assertThat(settingsService.findOneByMemberId(member.getId()).getDeadline_alarm_term()).isEqualTo(30);
//    }

}
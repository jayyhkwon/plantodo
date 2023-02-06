package demo.plantodo.form;

import demo.plantodo.converter.StringToEnumConverterFactory;
import demo.plantodo.domain.PermStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SettingsUpdateForm {
    private Long settings_id;
    private PermStatus notification_perm;
    private boolean deadline_alarm;
    private int deadline_alarm_term;

    public SettingsUpdateForm(PermStatus notification_perm, boolean deadline_alarm, int deadline_alarm_term, Long settings_id) {
        this.settings_id = settings_id;
        this.notification_perm = notification_perm;
        this.deadline_alarm = deadline_alarm;
        this.deadline_alarm_term = deadline_alarm_term;
    }

    /*String이 들어오는 경우*/
    public SettingsUpdateForm(String notification_perm, boolean deadline_alarm, int deadline_alarm_term, Long settings_id) {
        this(new StringToEnumConverterFactory().getConverter(PermStatus.class).convert(notification_perm), deadline_alarm, deadline_alarm_term, settings_id);
    }
}

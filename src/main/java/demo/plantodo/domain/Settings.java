package demo.plantodo.domain;

import demo.plantodo.converter.StringToEnumConverterFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Settings {
    @Id @GeneratedValue
    @Column(name = "settings_id")
    private Long id;

    /*Notification Permission*/
    @Enumerated(EnumType.STRING)
    private PermStatus notification_perm;

    /*토스트 알림*/
    private boolean deadline_alarm;
    private int deadline_alarm_term;

    public Settings(PermStatus notification_perm) {
        this.notification_perm = notification_perm;
        this.deadline_alarm = false;
        this.deadline_alarm_term = 0;
    }

    /*string이 들어가 있을 때*/
    public Settings(String notification_perm) {
        this(new StringToEnumConverterFactory().getConverter(PermStatus.class).convert(notification_perm));
    }
}

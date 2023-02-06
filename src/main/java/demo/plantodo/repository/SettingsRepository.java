package demo.plantodo.repository;

import demo.plantodo.domain.Settings;
import demo.plantodo.form.SettingsUpdateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
@Transactional
@RequiredArgsConstructor
public class SettingsRepository {

    private final EntityManager em;

    public void save(Settings settings) {
        em.persist(settings);
    }

    public Settings findOne(Long id) {
        return em.find(Settings.class, id);
    }

    public void remove(Settings settings) {
        em.remove(settings);
    }

    public void update(Long settingsId, SettingsUpdateForm settingsUpdateForm) {
        Settings settings = findOne(settingsId);
        settings.setNotification_perm(settingsUpdateForm.getNotification_perm());
        settings.setDeadline_alarm(settingsUpdateForm.isDeadline_alarm() ? true : false);
        settings.setDeadline_alarm_term(settingsUpdateForm.getDeadline_alarm_term());
    }
}

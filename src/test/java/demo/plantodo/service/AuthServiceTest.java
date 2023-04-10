package demo.plantodo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    public void saveOnlyAuthTest() throws Exception {
        String realKey = authService.save(1L);
        String testKey = authService.getKeyByMemberId(1L);
        Assertions.assertThat(testKey).isEqualTo(realKey);
    }

    @Test
    public void getMemberIdTest() throws Exception {
        String realKey = authService.save(1L);
        Assertions.assertThat(authService.getMemberIdByKey(realKey)).isEqualTo(1L);
    }
}
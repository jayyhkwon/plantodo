package demo.plantodo.service;

import demo.plantodo.domain.Auth;
import demo.plantodo.security.Encrypt;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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
}
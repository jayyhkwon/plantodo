package demo.plantodo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SseControllerTest {

    @Autowired
    private MockMvc mock;

    @Autowired
    private SseController sseController;

    @Autowired
    private MemberController memberController;

    @Test
    public void sseOpenExceptionTest() throws Exception {
        // given
        MockHttpServletRequestBuilder builder = post("/member/login")
                .param("email", "test@abc.co.kr")
                .param("password", "abc123!@#");
        // when - then
        mock.perform(builder)
                .andExpect(status().is4xxClientError());
    }
}
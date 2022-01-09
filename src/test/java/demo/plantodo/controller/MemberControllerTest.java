package demo.plantodo.controller;

import demo.plantodo.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired MemberController memberController;

    @Autowired
    private MockMvc mock;

    @Test
    public void 중복회원가입테스트() throws Exception {
        //given

        MockHttpServletRequestBuilder builder1 = post("/member/join")
                .param("email", "hcjy97@naver.com")
                .param("password", "12345678")
                .param("nickname", "nick");

        MockHttpServletRequestBuilder builder2 = post("/member/join")
                .param("email", "hcjy97@naver.com")
                .param("password", "50711234")
                .param("nickname", "brown");

        //when & when
        mock.perform(builder1)
                .andExpect(view().name("/member/login-form"))
                .andDo(print());

        mock.perform(builder2)
                .andExpect(view().name("/member/join-form"))
                .andExpect(model().attributeExists("errors"))
                .andDo(print());
    }


    @Test
    public void joinToLoginRedirect404Test() throws Exception {
        /*정상 값이 들어가면 302 redirect*/
        mock.perform(post("/join")
                .param("email", "test@abc.co.kr")
                .param("password", "abc123!@#")
                .param("nickname", "test"))
                .andExpect(status().isFound());
    }
}
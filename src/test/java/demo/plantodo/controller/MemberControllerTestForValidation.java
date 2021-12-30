package demo.plantodo.controller;

import demo.plantodo.form.MemberJoinForm;
import demo.plantodo.validation.MemberJoinlValidator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class MemberControllerTestForValidation {
    @Autowired
    MemberJoinlValidator memberJoinlValidator;

    @Autowired
    private MockMvc mockMvc;

    /*password*/
    @Test
    public void password_formal_normal() throws Exception {
        //given ~ when ~ then
        /*정상 값이 들어가면 302 isFound (로그인 창으로 redirect)*/
        mockMvc.perform(post("/member/join")
                        .param("email", "dpffpsk907@naver.com")
                        .param("password", "123abc+_)")
                        .param("nickname", "박하")
                        .flashAttr("memberJoinForm", new MemberJoinForm()))
                .andDo(print())
                .andExpect(status().isFound());

    }

    @Test
    public void password_formal_fail() throws Exception {
        /*영문, 숫자, 특수문자를 모두 포함하지 않으면 fail - 200 반환(이전 페이지로 돌아감)*/
        mockMvc.perform(post("/member/join")
                        .param("email", "dpffpsk907@gmail.com")
                        .param("password", "aaabbb)#(")
                        .param("nickname", "박하"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void password_range_min_normal() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "test@abc.co.kr")
                        .param("password", "abc123!@")
                        .param("nickname", "테스트"))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void password_range_max_normal() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "test@abc.co.kr")
                        .param("password", "abcdefghijklmnopqrs123!@")
                        .param("nickname", "테스트"))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void password_range_min_fail() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "test@abc.co.kr")
                        .param("password", "ab123!@")
                        .param("nickname", "테스트"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void password_range_max_fail() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "test@abc.co.kr")
                        .param("password", "abcdefghijklmnopqrstuvwxyz123!@")
                        .param("nickname", "테스트"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /*email*/
    @Test
    public void email_formal_normal() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "dpffpsk907@gmail.co.kr")
                        .param("password", "123abc+_)")
                        .param("nickname", "박하")
                        .flashAttr("memberJoinForm", new MemberJoinForm()))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void email_formal_fail() throws Exception {
        mockMvc.perform(post("/member/join")
                        .param("email", "te_st@abc.kr")
                        .param("password", "123abc+_)")
                        .param("nickname", "박하")
                        .flashAttr("memberJoinForm", new MemberJoinForm()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
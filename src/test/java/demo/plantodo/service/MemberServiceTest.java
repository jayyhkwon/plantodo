package demo.plantodo.service;

import demo.plantodo.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
class MemberServiceTest {
    @Autowired MemberService memberService;

    @Test
    public void 중복회원가입() throws Exception {
        //given
        Member member1 = new Member("dpffpsk907@gmail.com", "12345678", "nick");
        Member member2 = new Member("dpffpsk907@gmail.com", "12345678", "nick");
        //when
//        String member1Result = memberService.joinMember(member1);
//        String member2Result = memberService.joinMember(member2);

        //then
//        Assertions.assertThat(!member1Result.equals(member2Result));
    }
}
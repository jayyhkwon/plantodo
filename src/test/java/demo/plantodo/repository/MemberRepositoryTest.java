package demo.plantodo.repository;

import demo.plantodo.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입테스트() throws Exception {
        //given
        Member member = new Member("dpffpsk907@gmail.com", "50711234", "apple");

/*        //when
        String savedEmail = memberRepository.save(member);
        List<Member> memberByEmail = memberRepository.getMemberByEmail(savedEmail);
        //then
        Assertions.assertThat(memberByEmail.get(0).getEmail().equals(savedEmail));*/
    }
}
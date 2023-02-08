package demo.plantodo.service;

import demo.plantodo.domain.Member;
import demo.plantodo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public List<Member> getMemberByEmail(String email) {
        return memberRepository.getMemberByEmail(email);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}

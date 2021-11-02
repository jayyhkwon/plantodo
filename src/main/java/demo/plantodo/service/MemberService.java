package demo.plantodo.service;

import demo.plantodo.domain.Member;
import demo.plantodo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public String joinMember(Member member) {
        return memberRepository.save(member);
    }
}

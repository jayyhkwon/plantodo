package demo.plantodo.service;

import demo.plantodo.domain.Auth;
import demo.plantodo.domain.Member;
import demo.plantodo.repository.AuthRepository;
import demo.plantodo.repository.MemberRepository;
import demo.plantodo.security.Encrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    Encrypt encrypt = new Encrypt();
    private final AuthRepository authRepository;
    private final MemberRepository memberRepository;

    public String save(Member member) {
        String authKey = encrypt.getEncrypt(String.valueOf(member.getId()), encrypt.getSalt());

        Auth auth = new Auth(authKey, member);
        authRepository.save(auth);
        return auth.getKey_sha256();
    }

    public String getKeyByMemberId(Long memberId) {
        Auth auth = authRepository.findOneByMemberId(memberId);
        return auth.getKey_sha256();
    }

    public Long getMemberIdByKey(String key_sha256) {
        Auth auth = authRepository.findOneBySha256(key_sha256);
        return auth.getMember().getId();
    }
}

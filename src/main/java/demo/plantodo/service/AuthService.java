package demo.plantodo.service;

import demo.plantodo.domain.Auth;
import demo.plantodo.repository.AuthRepository;
import demo.plantodo.security.Encrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    Encrypt encrypt = new Encrypt();
    private final AuthRepository authRepository;

    public String save(Long memberId) {
        String authKey = encrypt.getEncrypt(String.valueOf(memberId), encrypt.getSalt());

        Auth auth = new Auth(authKey, memberId);
        authRepository.save(auth);
        return auth.getKey_sha256();
    }

    public String getKeyByMemberId(Long memberId) {
        Auth auth = authRepository.findOneByMemberId(memberId);
        return auth.getKey_sha256();
    }

    public Long getMemberIdByKey(String key_sha256) {
        Auth auth = authRepository.findOne(key_sha256);
        return auth.getMemberId();
    }
}

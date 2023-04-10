package demo.plantodo.repository;

import demo.plantodo.domain.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class AuthRepository {

    private final EntityManager em;
    public void save(Auth auth) {
        em.persist(auth);
    }

    public Auth findOne(String key_sha256) {
        return em.find(Auth.class, key_sha256);
    }

    public Auth findOneByMemberId(Long memberId) {
        return em.createQuery("select au from Auth au where au.memberId = :memberId", Auth.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }
}

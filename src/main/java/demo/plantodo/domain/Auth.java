package demo.plantodo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auth {

    @Id @GeneratedValue
    @Column(name = "auth_id")
    private Long id;

    private String key_sha256;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Auth(String key_sha256, Member member) {
        this.key_sha256 = key_sha256;
        this.member = member;
    }
}

package demo.plantodo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Auth {
    @Id
    private String key_sha256;
    private Long memberId;

    public Auth(String key_sha256, Long memberId) {
        this.key_sha256 = key_sha256;
        this.memberId = memberId;
    }
}

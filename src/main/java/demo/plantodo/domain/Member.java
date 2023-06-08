package demo.plantodo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @OneToOne
    @JoinColumn(name = "settings_id")
    private Settings settings;

    @OneToOne
    @JoinColumn(name = "auth_id")
    private Auth auth;

    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public Member(String email, String password, String nickname, Settings settings) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.settings = settings;
    }

    public Member(String email, String password, String nickname, Settings settings, Auth auth) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.settings = settings;
        this.auth = auth;
    }
}

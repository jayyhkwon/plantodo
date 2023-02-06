package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
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

    public Member() {
    }

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

}

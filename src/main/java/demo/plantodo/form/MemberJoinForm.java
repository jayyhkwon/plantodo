package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class MemberJoinForm {
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    private String permission;

    public MemberJoinForm() {
    }

    public MemberJoinForm(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public MemberJoinForm(String email, String password, String nickname, String permission) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.permission = permission;
    }
}

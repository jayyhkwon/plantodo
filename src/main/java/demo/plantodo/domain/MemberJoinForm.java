package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberJoinForm {
    private String email;
    private String password;
    private String nickname;
}

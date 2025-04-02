package maite.maite.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {
    private String email;
    private String password;
    private String name;
    // 필요한 다른 사용자 정보 필드 추가
}
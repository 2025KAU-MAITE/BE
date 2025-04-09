package maite.maite.web.dto;

import lombok.Getter;
import lombok.Setter;
import maite.maite.domain.Enum.Gender;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private Gender gender;
    private String phonenumber;
}

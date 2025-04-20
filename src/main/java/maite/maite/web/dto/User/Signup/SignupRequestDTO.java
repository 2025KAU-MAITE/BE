package maite.maite.web.dto.User.Signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import maite.maite.domain.Enum.Gender;

public class SignupRequestDTO {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SignupRequest {
        private String email;
        private String password;
        private String name;
        private String phonenumber;
        private String address;
    }
}

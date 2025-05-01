package maite.maite.web.dto.User.Signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SocialSignupResponseDTO {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class socialSignupResponse {
        private String email;
        private String name;
        private String phonenumber;
        private String address;
        private String provider;
    }
}

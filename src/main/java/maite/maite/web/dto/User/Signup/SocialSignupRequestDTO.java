package maite.maite.web.dto.User.Signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SocialSignupRequestDTO {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class SocialAdditionalInfo {
        private String phonenumber;
        private String address;
    }
}

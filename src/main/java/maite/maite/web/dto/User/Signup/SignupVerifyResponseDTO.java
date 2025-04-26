package maite.maite.web.dto.User.Signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignupVerifyResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class sendCodeForFindIdDto {
        private String message;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class signupCodeResponseDto {
        private boolean status;
        private String message;
    }
}
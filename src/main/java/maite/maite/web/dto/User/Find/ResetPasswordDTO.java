package maite.maite.web.dto.User.Find;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ResetPasswordDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class sendCodeForResetPasswordDto {
        private String message;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class resetCodeResponseDto {
        private boolean status;
        private String message;
    }
}
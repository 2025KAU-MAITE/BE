package maite.maite.web.dto.User.Find;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FindResponseDTO {
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
    public static class findIdResponseDto {
        private boolean status;
        private String email;
        private String message;
    }
}

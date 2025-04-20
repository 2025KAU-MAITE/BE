package maite.maite.web.dto.User.Signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SignupResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResponse {
        private Long userId;          // 생성된 사용자의 ID
        private String email;         // 가입한 이메일
        private String name;          // 사용자 이름
        private boolean isRegistered; // 가입 성공 여부
        private LocalDateTime registeredAt; // 가입 일시
        private String message;       // 응답 메시지
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingupCheckResponse {
        private boolean isDuplicated;
        private String message;
    }
}
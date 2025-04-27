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
    public static class sendCodeRequestForSignup {
        private String phonenumber;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class verifyCodeRequest {
        private String phonenumber;
        private String verificationCode;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class sendCodeRequestForEmail {
        private String name;
        private String phonenumber;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class verifyCodeRequestForEmail{
        private String name;
        private String phonenumber;
        private String verificationCode;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class sendCodeRequestForPass {
        private String name;
        private String email;
        private String phonenumber;
    }


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

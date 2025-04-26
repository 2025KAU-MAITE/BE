package maite.maite.web.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.service.auth.SmsService;
import maite.maite.service.auth.VerificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Tag(name = "sms", description = "SMS 인증 관련 API")
public class SmsController {
    private final SmsService smsService;
    private final VerificationService verificationService;

    @GetMapping("/send")
    @Operation(summary = "SMS 인증번호 발송 API")
    public ApiResponse<Void> sendSms(
            @Parameter(description = "전화번호") @RequestParam String phonenumber
    ) {
        System.out.println("API 호출됨: " + phonenumber);
        String verifcationCode = smsService.generateVerificationCode();

        boolean isSent = smsService.sendVerificationSms(phonenumber, verifcationCode);

        if (isSent) {
            verificationService.saveVerification(phonenumber, verifcationCode, 5);
            return ApiResponse.onSuccess(null);
        } else {
            return ApiResponse.onFailure("SMS4001", "인증번호 발송 실패", null);
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "SMS 인증번호 검증 API")
    public ApiResponse<Boolean> verifySms(
            @Parameter(description = "전화번호") @RequestParam String phonenumber,
            @Parameter(description = "인증코드") @RequestParam String code
    ) {
        boolean isValid = verificationService.verifyCode(phonenumber, code);

        if (isValid) {
            return ApiResponse.onSuccess(true);
        } else {
            return ApiResponse.onFailure("INVALID_VERIFICATION_CODE", "인증번호가 올바르지 않거나 만료되었습니다.", null);
        }
    }
}

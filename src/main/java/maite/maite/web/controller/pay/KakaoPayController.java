package maite.maite.web.controller.pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.pay.KakaoPayServiceImpl;
import maite.maite.web.dto.pay.KakaoPayApproveRequest;
import maite.maite.web.dto.pay.KakaoPayApproveResponse;
import maite.maite.web.dto.pay.KakaoPayReadyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kakao")
public class KakaoPayController {

    private final KakaoPayServiceImpl kakaoPayService;

    @PostMapping("/ready")
    public ApiResponse<KakaoPayReadyResponse> readyToPay(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        KakaoPayReadyResponse response = kakaoPayService.readyToPay(user);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/success")
    public ApiResponse<KakaoPayApproveResponse> approvePayment(
            @RequestBody KakaoPayApproveRequest request
    ) {
        KakaoPayApproveResponse response = kakaoPayService.approvePay(request);
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/cancel")
    public String cancel() {
        log.info("Payment cancelled");
        return "결제가 취소되었습니다.";
    }

    @GetMapping("/fail")
    public String fail() {
        log.info("Payment failed");
        return "결제가 실패했습니다.";
    }
}
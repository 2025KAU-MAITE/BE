package maite.maite.web.controller.pay;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.pay.KakaoPayService;
import maite.maite.service.pay.SubscriptionService;
import maite.maite.web.dto.pay.KakaoPayApproveRequest;
import maite.maite.web.dto.pay.KakaoPayApproveResponse;
import maite.maite.web.dto.pay.KakaoPayReadyResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "KakaoPay", description = "카카오페이 결제 관련 API")
@RequestMapping("/kakao")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/ready")
    @Operation(summary = "결제 요청 API", description = "결제 요청")
    public ApiResponse<KakaoPayReadyResponse> readyToPay(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        KakaoPayReadyResponse response = kakaoPayService.readyToPay(user);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/success")
    @Operation(summary = "결제 성공 API", description = "결제 성공")
    public ApiResponse<KakaoPayApproveResponse> approvePayment(
            @RequestBody KakaoPayApproveRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        KakaoPayApproveResponse response = kakaoPayService.approvePay(request);
        subscriptionService.addSubscription(user);

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
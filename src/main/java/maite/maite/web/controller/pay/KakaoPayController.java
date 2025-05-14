package maite.maite.web.controller.pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<KakaoPayReadyResponse> readyToPay(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        KakaoPayReadyResponse response = kakaoPayService.readyToPay(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<KakaoPayApproveResponse> approvePayment(
            @RequestParam("pg_token") String pgToken,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String userId) {

        log.info("Payment approval request received: pgToken={}, orderId={}, userId={}",
                pgToken, orderId, userId);

        KakaoPayApproveRequest request = KakaoPayApproveRequest.builder()
                .pgToken(pgToken)
                .partnerOrderId(orderId)
                .partnerUserId(userId)
                .build();

        KakaoPayApproveResponse response = kakaoPayService.approvePay(request);
        return ResponseEntity.ok(response);
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
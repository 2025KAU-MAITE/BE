package maite.maite.web.controller.pay;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.pay.SubscriptionService;
import maite.maite.service.pay.TossPayService;
import maite.maite.web.dto.pay.TossPayApproveRequest;
import maite.maite.web.dto.pay.TossPayApproveResponse;
import maite.maite.web.dto.pay.TossPayReadyResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "TossPay", description = "토스페이먼츠 결제 API")
@RequestMapping("/toss")
public class TossPayController {

    private final TossPayService tossPayService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/ready")
    @Operation(summary = "토스페이먼츠 결제 준비", description = "토스 SDK에서 사용할 결제 정보 생성")
    public ApiResponse<TossPayReadyResponse> readyToPay(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        try {
            User user = userDetails.getUser();
            log.info("토스페이먼츠 결제 준비 요청 - 사용자: {}", user.getEmail());

            TossPayReadyResponse response = tossPayService.readyToPay(user);

            log.info("토스페이먼츠 결제 준비 성공 - 주문ID: {}, 금액: {}원",
                    response.getOrderId(), response.getAmount());

            return ApiResponse.onSuccess(response);

        } catch (Exception e) {
            log.error("토스페이먼츠 결제 준비 실패: {}", e.getMessage(), e);
            return ApiResponse.onFailure("TOSS_READY_ERROR",
                    "결제 준비 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    @PostMapping("/confirm")
    @Operation(summary = "토스페이먼츠 결제 승인", description = "토스 SDK 결제 완료 후 최종 승인 처리")
    public ApiResponse<TossPayApproveResponse> confirmPayment(
            @RequestBody TossPayApproveRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        try {
            User user = userDetails.getUser();
            log.info("토스페이먼츠 결제 승인 요청 - 사용자: {}, 결제키: {}",
                    user.getEmail(), request.getPaymentKey());

            TossPayApproveResponse response = tossPayService.approvePay(request);

            if ("DONE".equals(response.getStatus()) || "WAITING_FOR_DEPOSIT".equals(response.getStatus())) {
                subscriptionService.addSubscription(user);
                log.info("토스페이먼츠 결제 완료 및 구독 추가 - 사용자: {}, 주문ID: {}",
                        user.getEmail(), response.getOrderId());
            } else {
                log.warn("결제 상태 미완료 - 상태: {}", response.getStatus());
            }

            return ApiResponse.onSuccess(response);

        } catch (Exception e) {
            log.error("토스페이먼츠 결제 승인 실패: {}", e.getMessage(), e);
            return ApiResponse.onFailure("TOSS_CONFIRM_ERROR",
                    "결제 승인 중 오류가 발생했습니다: " + e.getMessage(), null);
        }
    }

    @GetMapping("/success")
    @Operation(summary = "토스페이먼츠 결제 성공 처리", description = "토스 SDK에서 성공 시 호출되는 엔드포인트")
    public ApiResponse<String> successPage(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount
    ) {
        log.info("토스페이먼츠 결제 성공 - 결제키: {}, 주문ID: {}, 금액: {}원",
                paymentKey, orderId, amount);

        return ApiResponse.onSuccess(String.format(
                "토스페이먼츠 결제가 성공적으로 완료되었습니다.\n" +
                        "주문번호: %s\n" +
                        "결제금액: %,d원\n" +
                        "결제키: %s",
                orderId, amount, paymentKey));
    }

    @GetMapping("/fail")
    @Operation(summary = "토스페이먼츠 결제 실패 처리", description = "토스 SDK에서 실패 시 호출되는 엔드포인트")
    public ApiResponse<String> failPage(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId
    ) {
        log.warn("토스페이먼츠 결제 실패 - 주문ID: {}, 오류코드: {}, 메시지: {}",
                orderId, code, message);

        return ApiResponse.onFailure("TOSS_PAYMENT_FAILED",
                String.format("토스페이먼츠 결제가 실패했습니다.\n오류: %s\n주문번호: %s", message, orderId),
                null);
    }

    @GetMapping("/cancel")
    @Operation(summary = "토스페이먼츠 결제 취소", description = "사용자가 결제를 취소한 경우")
    public ApiResponse<String> cancelPage(
            @RequestParam(required = false) String orderId
    ) {
        log.info("토스페이먼츠 결제 취소 - 주문ID: {}", orderId);
        return ApiResponse.onSuccess("토스페이먼츠 결제가 취소되었습니다.");
    }
}
package maite.maite.service.pay;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.domain.entity.User;
import maite.maite.web.dto.pay.TossPayApproveRequest;
import maite.maite.web.dto.pay.TossPayApproveResponse;
import maite.maite.web.dto.pay.TossPayReadyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPayServiceImpl implements TossPayService {

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Value("${toss.client-key}")
    private String clientKey;

    @Value("${toss.secret-key}")
    private String secretKey;

    // 토스페이먼츠 실제 API 엔드포인트
    private static final String TOSS_API_BASE_URL = "https://api.tosspayments.com/v1";
    private static final String PAYMENT_CONFIRM_URL = TOSS_API_BASE_URL + "/payments/confirm";

    @Override
    public TossPayReadyResponse readyToPay(User user) {
        log.info("토스페이먼츠 결제 준비 - 사용자: {}", user.getEmail());

        // 주문 ID 생성 (고유값)
        String orderId = "TOSS_ORDER_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 서버 URL 동적 생성
        String baseUrl = getBaseUrl();
        String successUrl = baseUrl + "/toss/success";
        String failUrl = baseUrl + "/toss/fail";

        // 토스 SDK에서 사용할 결제 정보 생성
        TossPayReadyResponse response = new TossPayReadyResponse();
        response.setOrderId(orderId);
        response.setOrderName("MAITE 프리미엄 구독");
        response.setAmount(20000);
        response.setCustomerName(user.getName());
        response.setSuccessUrl(successUrl);
        response.setFailUrl(failUrl);
        response.setCreatedAt(LocalDateTime.now().toString());

        log.info("토스페이먼츠 결제 준비 완료 - 주문ID: {}", orderId);

        return response;
    }

    @Override
    public TossPayApproveResponse approvePay(TossPayApproveRequest requestDto) {
        log.info("토스페이먼츠 결제 승인 요청 - 결제키: {}, 주문ID: {}, 금액: {}",
                requestDto.getPaymentKey(), requestDto.getOrderId(), requestDto.getAmount());

        try {
            // 토스페이먼츠 실제 API 호출을 위한 헤더 생성
            HttpHeaders headers = createTossHeaders();

            // 요청 바디 생성 (토스 공식 문서 형식)
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("paymentKey", requestDto.getPaymentKey());
            bodyMap.put("orderId", requestDto.getOrderId());
            bodyMap.put("amount", requestDto.getAmount());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

            log.debug("토스페이먼츠 API 호출 시작");

            // 실제 토스페이먼츠 API 호출 (테스트 키 사용시 실제 돈은 빠지지 않음)
            ResponseEntity<TossPayApproveResponse> response =
                    restTemplate.postForEntity(PAYMENT_CONFIRM_URL, requestEntity, TossPayApproveResponse.class);

            TossPayApproveResponse approveResponse = response.getBody();

            if (approveResponse != null) {
                log.info("토스페이먼츠 결제 승인 성공 - 주문ID: {}, 상태: {}, 결제수단: {}, 금액: {}원",
                        approveResponse.getOrderId(), approveResponse.getStatus(),
                        approveResponse.getMethod(), approveResponse.getTotalAmount());

                if (approveResponse.getCard() != null) {
                    log.debug("결제 카드 정보 - 카드사: {}, 카드번호: {}",
                            approveResponse.getCard().getCompany(), approveResponse.getCard().getNumber());
                }
            }

            return approveResponse;

        } catch (Exception e) {
            log.error("토스페이먼츠 결제 승인 실패: {}", e.getMessage(), e);
            throw new RuntimeException("토스페이먼츠 결제 승인에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 토스페이먼츠 API 호출용 헤더 생성
     * Basic 인증 사용 (시크릿 키를 Base64 인코딩)
     */
    private HttpHeaders createTossHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 토스페이먼츠 인증: 시크릿 키를 Base64 인코딩
        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
    }

    /**
     * 서버 기본 URL 생성
     */
    private String getBaseUrl() {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        if ("localhost".equals(serverName)) {
            return "http://localhost:" + serverPort;
        } else {
            return "http://3.39.205.32:8080"; // 실제 서버 URL
        }
    }
}
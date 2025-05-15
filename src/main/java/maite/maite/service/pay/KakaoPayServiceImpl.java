package maite.maite.service.pay;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.domain.entity.User;
import maite.maite.web.dto.pay.KakaoPayApproveRequest;
import maite.maite.web.dto.pay.KakaoPayApproveResponse;
import maite.maite.web.dto.pay.KakaoPayReadyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayServiceImpl implements KakaoPayService {

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Value("${kakao.admin-key}")
    private String adminKey;

    private static final String CID = "TC0ONETIME";
    private static final String READY_URL = "https://kapi.kakao.com/v1/payment/ready";
    private static final String APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

    @Override
    public KakaoPayReadyResponse readyToPay(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 고정된 값으로 주문 ID 생성
        String orderId = "ORDER_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userId = "USER_" + user.getId();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", CID);
        body.add("partner_order_id", orderId);
        body.add("partner_user_id", userId);
        body.add("item_name", "테스트 요금제");
        body.add("quantity", "1");
        body.add("total_amount", "20000");
        body.add("tax_free_amount", "0");

        String serverName = request.getServerName();
        String baseUrl;

        if (serverName.equals("localhost")) {
            baseUrl = "http://localhost:8080";
        } else {
            baseUrl = "http://3.39.205.32"; // 또는 DNS 도메인으로 대체
        }

        body.add("approval_url", baseUrl + "/kakao/success");
        body.add("cancel_url", baseUrl + "/kakao/cancel");
        body.add("fail_url", baseUrl + "/kakao/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<KakaoPayReadyResponse> response =
                restTemplate.postForEntity(READY_URL, request, KakaoPayReadyResponse.class);

        KakaoPayReadyResponse readyResponse = response.getBody();

        if (readyResponse != null) {
            readyResponse.setPartnerOrderId(orderId);
            readyResponse.setPartnerUserId(userId);
        }
        return readyResponse;
    }

    @Override
    public KakaoPayApproveResponse approvePay(KakaoPayApproveRequest requestDto) {
        String tid = requestDto.getTid();
        String pgToken = requestDto.getPgToken();
        String orderId = requestDto.getPartnerOrderId();
        String userId = requestDto.getPartnerUserId();

        // 🔐 필수값 검증
        if (tid == null || pgToken == null || orderId == null || userId == null) {
            throw new IllegalArgumentException("결제 승인에 필요한 파라미터가 누락되었습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", CID);
        body.add("tid", tid);
        body.add("partner_order_id", orderId);
        body.add("partner_user_id", userId);
        body.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KakaoPayApproveResponse> response =
                    restTemplate.postForEntity(APPROVE_URL, request, KakaoPayApproveResponse.class);

            return response.getBody();
        } catch (Exception e) {
            log.error("카카오페이 승인 요청 실패 - {}", e.getMessage(), e);
            throw new RuntimeException("카카오페이 결제 승인 중 오류가 발생했습니다.", e);
        }
    }
}

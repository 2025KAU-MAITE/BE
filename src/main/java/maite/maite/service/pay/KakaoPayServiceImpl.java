package maite.maite.service.pay;

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
    private final HttpSession session;

    @Value("${kakao.admin-key}")
    private String adminKey;

    private static final String CID = "TC0ONETIME";
    private static final String READY_URL = "https://kapi.kakao.com/v1/payment/ready";
    private static final String APPROVE_URL = "https://kapi.kakao.com/v1/payment/approve";

    @Override
    public KakaoPayReadyResponse readyToPay(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey); // ✅ 여기서 prefix 붙여줌
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
        // URL 파라미터로 주문 정보 전달
        body.add("approval_url", "http://localhost:8080/kakao/success");
        body.add("cancel_url", "http://localhost:8080/kakao/cancel");
        body.add("fail_url", "http://localhost:8080/kakao/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<KakaoPayReadyResponse> response =
                restTemplate.postForEntity(READY_URL, request, KakaoPayReadyResponse.class);

        KakaoPayReadyResponse readyResponse = response.getBody();

        if (readyResponse != null) {
            // 결제 정보를 세션에 저장
            session.setAttribute("kakaoPayTid", readyResponse.getTid());
            session.setAttribute("kakaoPayOrderId", orderId);
            session.setAttribute("kakaoPayUserId", userId);
        }

        return readyResponse;
    }

    @Override
    public KakaoPayApproveResponse approvePay(KakaoPayApproveRequest requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String tid = requestDto.getTid();
        String pgToken = requestDto.getPgToken();
        String orderId = requestDto.getPartnerOrderId();
        String userId = requestDto.getPartnerUserId();

        // URL 파라미터에서 값이 제공되지 않으면 세션에서 가져오기
        if (tid == null || tid.isEmpty()) {
            tid = (String) session.getAttribute("kakaoPayTid");
        }

        if (orderId == null || orderId.isEmpty()) {
            orderId = (String) session.getAttribute("kakaoPayOrderId");
        }

        if (userId == null || userId.isEmpty()) {
            userId = (String) session.getAttribute("kakaoPayUserId");
        }

        // 안전장치 - 모든 필수값 확보 확인
        if (tid == null || pgToken == null || orderId == null || userId == null) {
            throw new IllegalArgumentException("Missing required payment parameters");
        }

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

            // 결제 완료 후 세션 정리
            session.removeAttribute("kakaoPayTid");
            session.removeAttribute("kakaoPayOrderId");
            session.removeAttribute("kakaoPayUserId");

            return response.getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}

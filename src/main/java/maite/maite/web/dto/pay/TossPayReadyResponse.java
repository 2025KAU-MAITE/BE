package maite.maite.web.dto.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPayReadyResponse {
    private String paymentKey;           // 결제의 키 값
    private String orderId;              // 주문ID
    private String orderName;            // 주문명
    private Integer amount;              // 결제할 금액
    private String customerName;         // 고객 이름
    private String successUrl;           // 결제 성공시 리다이렉트 URL
    private String failUrl;              // 결제 실패시 리다이렉트 URL
    private String createdAt;            // 결제가 생성된 시각
    private String checkoutUrl;          // 결제창 URL
}


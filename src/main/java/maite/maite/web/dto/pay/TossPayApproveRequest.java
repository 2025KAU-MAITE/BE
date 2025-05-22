package maite.maite.web.dto.pay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPayApproveRequest {
    private String paymentKey;    // 결제 고유 키
    private String orderId;       // 주문 ID
    private Integer amount;       // 결제 금액
}


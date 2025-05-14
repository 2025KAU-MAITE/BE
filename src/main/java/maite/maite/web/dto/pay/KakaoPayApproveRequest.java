package maite.maite.web.dto.pay;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveRequest {
    private String tid;
    private String pgToken;
    private String partnerOrderId;
    private String partnerUserId;
}
package maite.maite.web.dto.pay;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoPayApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String partnerOrderId;
    private String partnerUserId;
    private String paymentMethodType;
    private Amount amount;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private String createdAt;
    private String approvedAt;

    @Getter
    @NoArgsConstructor
    public static class Amount {
        private Integer total;
        private Integer taxFree;
        private Integer vat;
        private Integer point;
        private Integer discount;
    }
}
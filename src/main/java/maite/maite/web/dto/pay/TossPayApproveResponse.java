package maite.maite.web.dto.pay;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossPayApproveResponse {
    private String paymentKey;       // 결제의 키 값
    private String orderId;          // 주문ID
    private String orderName;        // 주문명
    private String status;           // 결제 처리 상태
    private String requestedAt;      // 결제가 일어난 날짜와 시간
    private String approvedAt;       // 결제 승인이 일어난 날짜와 시간
    private Boolean useEscrow;       // 에스크로 사용 여부
    private String cultureExpense;   // 문화비 지출 여부
    private Card card;              // 카드로 결제했을 때 제공되는 카드 관련 정보
    private Integer totalAmount;     // 총 결제 금액
    private Integer balanceAmount;   // 취소할 수 있는 금액
    private Integer suppliedAmount;  // 공급가액
    private Integer vat;            // 부가세
    private String method;          // 결제수단

    @Getter
    @NoArgsConstructor
    public static class Card {
        private String company;      // 카드사
        private String number;       // 카드번호
        private String installmentPlanMonths; // 할부 개월 수
        private String isInterestFree; // 무이자 할부 여부
        private String approveNo;    // 카드사 승인 번호
        private String useCardPoint; // 카드포인트 사용 여부
        private String cardType;     // 카드 종류
        private String ownerType;    // 카드 소유자 타입
        private String receiptUrl;   // 카드 결제의 매출전표
    }
}
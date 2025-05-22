package maite.maite.service.pay;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.pay.TossPayApproveRequest;
import maite.maite.web.dto.pay.TossPayApproveResponse;
import maite.maite.web.dto.pay.TossPayReadyResponse;

public interface TossPayService {
    /**
     * 토스페이먼츠 결제 준비
     * @param user 결제 요청 사용자
     * @return 결제 준비 응답 (결제창 URL 포함)
     */
    TossPayReadyResponse readyToPay(User user);

    /**
     * 토스페이먼츠 결제 승인
     * @param requestDto 결제 승인 요청 정보
     * @return 결제 승인 응답
     */
    TossPayApproveResponse approvePay(TossPayApproveRequest requestDto);
}
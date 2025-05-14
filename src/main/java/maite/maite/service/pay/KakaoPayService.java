package maite.maite.service.pay;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.pay.KakaoPayApproveRequest;
import maite.maite.web.dto.pay.KakaoPayReadyResponse;
import maite.maite.web.dto.pay.KakaoPayApproveResponse;

public interface KakaoPayService {
    KakaoPayReadyResponse readyToPay(User user);
    KakaoPayApproveResponse approvePay(KakaoPayApproveRequest requestDto);
}

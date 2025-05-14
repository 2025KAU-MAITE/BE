package maite.maite.web.dto.pay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayReadyResponse {
    private String tid;                     // 결제 고유 번호
    private String next_redirect_mobile_url;    // 사용자 결제 페이지 URL
    private String created_at;
}
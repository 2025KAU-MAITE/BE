package maite.maite.web.dto.pay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayReadyResponse {
    private String tid;                     // 결제 고유 번호
    @JsonProperty("next_redirect_mobile_url")
    private String nextRedirectMobileUrl;    // 사용자 결제 페이지 URL
    private String created_at;
}
package maite.maite.web.dto.User.Login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequest {
    private String idToken;      // 구글에서 발급받은 ID 토큰 (JWT 형식)
    private String accessToken;  // 구글 API용 액세스 토큰 (사용자 정보 조회 등)
}
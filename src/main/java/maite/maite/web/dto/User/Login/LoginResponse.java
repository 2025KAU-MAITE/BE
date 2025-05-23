package maite.maite.web.dto.User.Login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String message;
    private Long userId;
}

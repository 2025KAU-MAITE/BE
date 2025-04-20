package maite.maite.web.dto.User.Logout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LogoutResponse {
    private String message;
}

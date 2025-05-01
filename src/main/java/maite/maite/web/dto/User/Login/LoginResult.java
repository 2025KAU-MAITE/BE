package maite.maite.web.dto.User.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maite.maite.domain.entity.User;

@Getter
@AllArgsConstructor
public class LoginResult {
    private User user;
    private String accessToken;
}
package maite.maite.service;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.LoginRequest;
import maite.maite.web.dto.SignupRequest;

public interface AuthService {
    void signup(SignupRequest signupRequest);
    String login(LoginRequest loginRequest);
    String reissueAccessToken(String refreshToken);
    void logout(User user);
}

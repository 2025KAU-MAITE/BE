package maite.maite.service.auth;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.User.Login.LoginRequest;
import maite.maite.web.dto.User.Login.LoginResult;
import maite.maite.web.dto.User.Signup.SignupRequestDTO;

public interface AuthService {
    boolean isDuplicated(String email);
    User signup(SignupRequestDTO signupRequest);
    LoginResult login(LoginRequest loginRequest);
    String reissueAccessToken(String refreshToken);
    void logout(User user);
    String findEmailByPhonenumber(String name, String phonenumber);
}

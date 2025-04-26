package maite.maite.service.auth;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.LoginProvider;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.security.JwtTokenProvider;
import maite.maite.web.dto.User.Login.LoginRequest;
import maite.maite.web.dto.User.Login.LoginResult;
import maite.maite.web.dto.User.Signup.SignupRequestDTO.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User signup(SignupRequest request) {
        if (isDuplicated(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider(LoginProvider.EMAIL)
                .phonenumber(request.getPhonenumber())
                .address(request.getAddress())
                .build();
        userRepository.save(user);
        return user;
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        String accessToken = jwtTokenProvider.createToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        LoginResult result = new LoginResult(user, accessToken);

        return result;
    }

    public String reissueAccessToken(String refreshToken) {

        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!refreshToken.equals(user.getRefreshToken())){
            throw new IllegalArgumentException("일치하지 않는 리프레시 토큰입니다.");
        }
        return jwtTokenProvider.createToken(email);
    }

    public void logout(User user) {
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    public String findEmailByPhonenumber(String phonenumber) {
        return userRepository.findByPhonenumber(phonenumber)
                .map(User::getEmail)
                .orElseThrow(()-> new IllegalArgumentException("해당 전화번호에 일치하는 아이디는 없습니다."));
    }
}

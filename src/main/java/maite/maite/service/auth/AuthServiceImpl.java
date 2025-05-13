package maite.maite.service.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.exception.handler.CommonExceptionHandler;
import maite.maite.domain.Enum.LoginProvider;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.security.GoogleTokenUtil;
import maite.maite.security.JwtTokenProvider;
import maite.maite.web.dto.User.Login.GoogleLoginRequest;
import maite.maite.web.dto.User.Login.LoginRequest;
import maite.maite.web.dto.User.Login.LoginResult;
import maite.maite.web.dto.User.Signup.SignupRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static maite.maite.apiPayload.code.status.ErrorStatus.MEMBER_NOT_FOUND;
import static maite.maite.apiPayload.code.status.ErrorStatus.USER_NOT_FOUND_FOR_FIND_EMAIL;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenUtil googleTokenUtil;

    public boolean isDuplicated(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User signup(SignupRequestDTO request) {
        if (isDuplicated(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        String profileImageUrl = request.getProfileImageUrl();
        if (profileImageUrl == null || profileImageUrl.trim().isEmpty()) {
            profileImageUrl = "https://maite-s3.s3.ap-northeast-2.amazonaws.com/profile/980edf16-2ec6-4505-893f-5e8d40f9d960.png";
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider(LoginProvider.EMAIL)
                .phonenumber(request.getPhonenumber())
                .address(request.getAddress())
                .profileImageUrl(profileImageUrl)
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

    public LoginResult googleLogin(GoogleLoginRequest googleLoginRequest) {
        GoogleIdToken.Payload payload = googleTokenUtil.verifyIdToken(googleLoginRequest.getIdToken());
        String email = payload.getEmail();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            String accessToken = jwtTokenProvider.createToken(user.get().getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.get().getEmail());
            user.get().setRefreshToken(refreshToken);
            userRepository.save(user.get());

            LoginResult result = new LoginResult(user.get(), accessToken);
            return result;
        } else {
            throw new CommonExceptionHandler(MEMBER_NOT_FOUND);
        }
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

    public String findEmailByPhonenumber(String name, String phonenumber) {
        return userRepository.findByNameAndPhonenumber(name, phonenumber)
                .map(User::getEmail)
                .orElseThrow(()-> new CommonExceptionHandler(USER_NOT_FOUND_FOR_FIND_EMAIL));
    }
}

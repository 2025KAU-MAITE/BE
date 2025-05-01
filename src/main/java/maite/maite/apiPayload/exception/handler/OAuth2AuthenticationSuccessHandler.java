package maite.maite.apiPayload.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 이메일 추출
        String email = oAuth2User.getAttribute("email");

        // DB에서 직접 사용자 조회
        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            System.out.println("기존");
            // 이미 회원가입된 사용자 - 토큰 발급 후 메인 페이지로 리디렉션
            User user = userOptional.get();

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createToken(user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            // 리프레시 토큰 저장
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            // 토큰과 함께 메인 페이지로 리디렉션
            String redirectUrl = "/main?accessToken=" + accessToken +
                    "&refreshToken=" + refreshToken;

            response.sendRedirect(redirectUrl);
        } else {
            System.out.println("신규");
            // 신규 사용자 - 추가 정보 입력이 필요한 경우
            String name = oAuth2User.getAttribute("name");

            // URL 인코딩 추가
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);

            System.out.println("인코딩된 이름: " + encodedName); // 디버깅용 로그
            System.out.println("인코딩된 이름: " + encodedEmail); // 디버깅용 로그

            // 추가 정보 입력 페이지로 리디렉션
            String redirectUrl = "/auth/additional-info?email=" + encodedEmail +
                    "&name=" + encodedName +
                    "&provider=GOOGLE";

            response.sendRedirect(redirectUrl);
        }
    }
}
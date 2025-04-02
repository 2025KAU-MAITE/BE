package maite.maite.apiPayLoad.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import maite.maite.config.JwtTokenProvider;
import maite.maite.domain.entity.RefreshToken;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.service.FirstLoginOAuth2UserService;
import maite.maite.service.RefreshTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (oAuth2User instanceof FirstLoginOAuth2UserService) {
            String email = oAuth2User.getAttribute("email");
            String json = String.format("{\"firstLogin\": true, \"email\": \"%s\"}", email);
            response.getWriter().write(json);
            return;
        }

        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        String json = String.format(
                "{\"firstLogin\": false, \"accessToken\": \"%s\", \"refreshToken\": \"%s\"}",
                accessToken, refreshToken.getToken()
        );        response.getWriter().write(json);
    }
}

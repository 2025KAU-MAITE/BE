package maite.maite.apiPayload.exception.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 인증 실패 시 에러 메시지와 함께 로그인 페이지로 리디렉션
        String errorMessage = exception.getMessage();
        String redirectUrl = "/login?error=social-login-failed&message=" + errorMessage;

        // 로그 남기기 (실패 이유 추적을 위해)
        System.out.println("OAuth2 인증 실패: " + errorMessage);

        response.sendRedirect(redirectUrl);
    }
}
package maite.maite.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerUserDetailsService customerUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
             HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Authorization 헤더에서 토큰 꺼내기
        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 이메일(또는 ID) 추출
            String email = jwtTokenProvider.getEmail(token);

            // 사용자 정보 조회
            var userDetails = customerUserDetailsService.loadUserByUsername(email);

            // 4. 인증 객체 생성
            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 5. SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}

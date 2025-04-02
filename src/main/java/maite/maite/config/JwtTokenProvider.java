package maite.maite.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import maite.maite.domain.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key;

    // ⬇️ 토큰 유효 시간: 1시간
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // ⬇️ yml에서 비밀 키 주입
    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * ✅ JWT 토큰 생성
     */
    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail()); // 사용자 식별자
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * ✅ 토큰에서 사용자 이메일 추출
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // → 이게 위에서 setSubject(email)한 값
    }
}

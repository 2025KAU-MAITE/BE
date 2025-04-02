package maite.maite.service;

import jakarta.transaction.Transactional;
import maite.maite.config.JwtTokenProvider;
import maite.maite.domain.entity.RefreshToken;
import maite.maite.domain.entity.User;
import maite.maite.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUserEmail(user.getEmail());
        String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserEmail(user.getEmail());
        refreshToken.setToken(jwtRefreshToken);
        refreshToken.setUser(user);

        Date expiryDate = jwtTokenProvider.getExpirationDateFromToken(jwtRefreshToken);
        refreshToken.setExpiryDate(Instant.ofEpochMilli(expiryDate.getTime()));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * 리프레시 토큰으로 토큰 정보 조회
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * 리프레시 토큰 만료 검증
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        // JWT 토큰 자체의 유효성 검사
        if (!jwtTokenProvider.validateToken(token.getToken())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("리프레시 토큰이 유효하지 않습니다. 다시 로그인해주세요.");
        }

        // 토큰 만료일 검사
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        return token;
    }
}

package maite.maite.repository;

import maite.maite.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 토큰 값으로 리프레시 토큰 조회
    Optional<RefreshToken> findByToken(String token);
    // 사용자 이메일로 리프레시 토큰 조회
    Optional<RefreshToken> findByUserEmail(String userEmail);

    // 사용자 이메일로 리프레시 토큰 삭제
    void deleteByUserEmail(String userEmail);

    // 토큰 존재 여부 확인
    boolean existsByToken(String token);
}

package maite.maite.repository;

import maite.maite.domain.Enum.MateStatus;
import maite.maite.domain.entity.Mate;
import maite.maite.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MateRepository extends JpaRepository<Mate, Long> {
    // 친구 목록 조회 (상태가 ACCEPTED인 경우만)
    List<Mate> findAllByUserAndStatus(User user, MateStatus status);

    // 받은 친구 요청 목록 조회
    List<Mate> findAllByMateUserAndStatus(User mateUser, MateStatus status);

    // 특정 친구 관계 조회
    Optional<Mate> findByUserAndMateUserAndStatus(User user, User mateUser, MateStatus status);

    // 친구 관계 존재 여부 확인 (상태 무관)
    boolean existsByUserAndMateUser(User user, User mateUser);

    // 친구 상태별 관계 존재 여부 확인
    boolean existsByUserAndMateUserAndStatus(User user, User mateUser, MateStatus status);

    //친구 카운트
    @Query("SELECT COUNT(m) FROM Mate m WHERE m.user = :user AND m.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") MateStatus status);

    // 검색 기능 유지
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND u.id != :userId")
    List<User> searchUsers(@Param("query") String query, @Param("userId") Long userId);
}


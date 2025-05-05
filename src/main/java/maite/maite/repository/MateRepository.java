package maite.maite.repository;

import maite.maite.domain.entity.Mate;
import maite.maite.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MateRepository extends JpaRepository<Mate, Long> {
    // Find all mates of a user
    List<Mate> findAllByUser(User user);

    // Find specific friendship
    Optional<Mate> findByUserAndMateUser(User user, User mateUser);

    // 친구 여부 확인
    boolean existsByUserAndMateUser(User user, User mateUser);

    // Search users by name or email for friend suggestions
    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) AND u.id != :userId")
    List<User> searchUsers(@Param("query") String query, @Param("userId") Long userId);
}

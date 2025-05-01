package maite.maite.repository.chat;

import maite.maite.domain.Enum.ChatRoomUserRole;
import maite.maite.domain.mapping.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.chatRoom.id = :roomId AND cru.user.id = :userId")
    Optional<ChatRoomUser> findByChatRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    boolean existsByChatRoomIdAndRoleAndUserIdNot(Long roomId, ChatRoomUserRole role, Long userId);

    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.chatRoom.id = :roomId AND cru.user.id != :userId")
    Optional<ChatRoomUser> findFirstByChatRoomIdAndUserIdNot(@Param("roomId") Long roomId, @Param("userId") Long userId);

    void deleteAllByChatRoomId(Long roomId);
    boolean existsByChatRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long nuwUserId);

}


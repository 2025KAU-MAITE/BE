package maite.maite.repository.chat;

import maite.maite.domain.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.chatRoomUsers cru WHERE cru.user.id = :userId")
    List<ChatRoom> findByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
            "JOIN cr.chatRoomUsers cru1 " +
            "JOIN cr.chatRoomUsers cru2 " +
            "WHERE cru1.user.id = :userId1 AND cru2.user.id = :userId2 " +
            "AND cr.isGroupChat = :isGroupChat")
    List<ChatRoom> findChatRoomsByUserIds(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("isGroupChat") boolean isGroupChat);
}

package maite.maite.repository.chat;

import maite.maite.domain.entity.chat.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId AND (:lastMessageId IS NULL OR m.id < :lastMessageId) ORDER BY m.id DESC")
    List<Message> findByChatRoomIdAndIdLessThanOrderByIdDesc(
            @Param("roomId") Long roomId,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable);

    // 채팅방의 메시지 조회 (최신순 정렬)
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId ORDER BY m.id DESC")
    List<Message> findByChatRoomIdOrderByIdDesc(
            @Param("roomId") Long roomId,
            Pageable pageable);
}

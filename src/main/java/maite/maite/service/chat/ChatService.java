package maite.maite.service.chat;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.chat.ChatRoom;
import maite.maite.web.dto.chat.response.ChatRoomResponseDto;
import maite.maite.web.dto.chat.response.MessageResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatService {
    //채팅방 관련
    ChatRoomResponseDto createPersonalChatRoom(Long userId, Long receiverId);
    ChatRoomResponseDto createGroupChatRoom(String roomName, Long userId, List<Long> userIds);
    List<ChatRoomResponseDto> getChatRoomsByUserId(Long userId);
    void leaveChatRoom(Long userId, Long roomId);
    void deleteChatRoom(Long userId, Long roomId);
    void inviteUserChatRoom(Long userId, Long roomId, List<Long> userIds);
    List<MessageResponseDto> getChatMessages(Long roomId, Long userId, Long lastMessageId);
    //메세지 관련
    MessageResponseDto sendTextMessage(Long roomId, Long userId, String content);
    MessageResponseDto sendImageMessage(Long roomId, Long userId, String imageUrl);

    //메세지 읽음 처리
    void markMessageAsRead(Long roomId, Long userId, Long messageId);

    //안읽은 메시지 수 조회
    //int getUnreadMessageCount(Long roomId, Long userId);

}

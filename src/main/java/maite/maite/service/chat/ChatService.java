package maite.maite.service.chat;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.chat.ChatRoom;
import maite.maite.web.dto.chat.response.ChatRoomResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatService {
    ChatRoomResponseDto createPersonalChatRoom(Long userId, Long receiverId);
    ChatRoomResponseDto createGroupChatRoom(String roomName, Long userId, List<Long> userIds);
    List<ChatRoomResponseDto> getChatRoomsByUserId(Long userId);
    void leaveChatRoom(Long userId, Long roomId);
    void deleteChatRoom(Long userId, Long roomId);
}

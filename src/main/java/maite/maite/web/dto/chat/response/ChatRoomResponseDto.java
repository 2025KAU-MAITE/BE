package maite.maite.web.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private Long id;
    private Long userId;
    private List<Long> receiverIds;
    private String roomName;
    private boolean isGroupChat;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private String profileImageUrl;
    private int participantCount;
}

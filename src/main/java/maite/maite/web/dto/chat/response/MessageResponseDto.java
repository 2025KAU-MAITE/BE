package maite.maite.web.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private Long id;               // 메시지 ID
    private Long roomId;           // 채팅방 ID
    private Long senderId;         // 발신자 ID
    private String senderName;     // 발신자 이름
    private String senderProfileImageUrl; // 발신자 프로필 이미지 URL
    private String content;        // 텍스트 내용 (이미지만 있는 경우 null 가능)
    private String imageUrl;       // 이미지 URL (텍스트만 있는 경우 null)
    private LocalDateTime sendAt;  // 전송 시간
    private boolean isRead;        // 읽음 여부
}
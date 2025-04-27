package maite.maite.web.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomGroupRequestDto {
    private String roomName;
    private List<Long> memberIds;
    private String profileImageUrl;
    private Integer maxMembers;
}

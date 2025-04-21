package maite.maite.web.dto.room.request;

import lombok.Getter;

@Getter
public class RoomInviteRequest {
    private String email; // 초대할 사용자 이메일
}
package maite.maite.web.dto.meeting.request;

import lombok.Getter;

@Getter
public class MeetingInviteRequest {
    private String email; // 초대할 사용자 이메일
}
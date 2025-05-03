package maite.maite.web.dto.meeting.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingMeetingResponse {
    private String email;
    private String name;
}

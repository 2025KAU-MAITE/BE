package maite.maite.web.dto.meeting.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class MeetingCreateResponse {
    private Long meetingId;
}

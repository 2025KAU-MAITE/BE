package maite.maite.web.dto.meeting.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MeetingSummaryResponse {
    private Long meetingId;
    private String title;
    private String proposerName;
    private String meetingDate;
    private String meetingTime;
    private String meetingEndTime;
    private String address;
    private String acceptance;
}

package maite.maite.web.dto.meeting.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingUpdateRequest{
    private String title;
    private String meetingDay;
    private String meetingTime;
    private String address;
}

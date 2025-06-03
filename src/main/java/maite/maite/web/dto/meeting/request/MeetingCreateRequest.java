package maite.maite.web.dto.meeting.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingCreateRequest{
    private String title;
    private String meetingDate;
    private String meetingTime;
    private String meetingEndTime;
    private List<String> inviteEmails;
}
package maite.maite.web.dto.meeting.response;

import jakarta.persistence.Lob;
import maite.maite.domain.entity.meeting.Meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingResponse{
    private Long meetingId;
    private String title;
    private String proposerName;
    private String meetingDate;
    private String meetingTime;
    private String meetingEndTime;
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
    private List<String> participantEmails;
    private String record;
    private String recordText;
    private String textSum;
    private LocalDateTime createdAt;
}
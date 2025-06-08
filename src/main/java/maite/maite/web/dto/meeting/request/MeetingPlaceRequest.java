package maite.maite.web.dto.meeting.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingPlaceRequest {
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
}
package maite.maite.web.dto.timetable.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequestDto {
    private String title;
    private String day;
    private String place;
    private String startTime;
    private String endTime;
}

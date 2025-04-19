package maite.maite.web.dto.timetable.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventResponseDto {
    private Long id;
    private String title;
    private String day;
    private String place;
    private String startTime;
    private String endTime;
}
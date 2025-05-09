package maite.maite.web.dto.timetable.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserTimetableResponseDto {
    private Long timetableId;
    private Long userId;
    private String userName;
    //private String userEmail;
    private int mateCount;
    private List<EventResponseDto> events;
}

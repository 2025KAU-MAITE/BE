package maite.maite.web.dto.timetable.response;

import lombok.Builder;
import lombok.Getter;
import maite.maite.web.dto.timetable.response.EventResponseDto;

import java.util.List;

@Getter
@Builder
public class TimetableResponseDto {
    private Long id;
    private Long userId;
    private List<EventResponseDto> events;
}

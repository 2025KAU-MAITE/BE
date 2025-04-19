package maite.maite.web.dto.timetable.response;

import lombok.Builder;
import lombok.Getter;
import maite.maite.web.dto.timetable.request.EventRequestDto;

import java.util.List;

@Getter
@Builder
public class TimetableResponseDto {
    private Long id;
    private Long userId;
    private List<EventRequestDto> events;
}

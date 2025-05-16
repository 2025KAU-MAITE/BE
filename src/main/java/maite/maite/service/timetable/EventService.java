package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;

import java.util.List;

public interface EventService {

    EventResponseDto getEvent(Long eventId, Long userId);
    List<EventResponseDto> getMyEvents(Long userId);
    EventResponseDto createEvent(EventRequestDto request, Long userId);
    EventResponseDto updateEvent(Long eventId, EventRequestDto request, Long userId);
    void deleteEvent(Long eventId, Long userId);
}

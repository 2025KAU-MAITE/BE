package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;

import java.util.List;

public interface EventService {
    EventResponseDto getEvent(Long eventId);
    List<EventResponseDto> getEventsByTimetable(Long timetableId);
    EventResponseDto createEvent(Long timetableId, EventRequestDto request);
    EventResponseDto updateEvent(Long eventId, EventRequestDto request);
    void deleteEvent(Long eventId);
}

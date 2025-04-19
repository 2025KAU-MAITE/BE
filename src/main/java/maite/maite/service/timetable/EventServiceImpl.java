package maite.maite.service.timetable;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.timetable.Event;
import maite.maite.domain.entity.timetable.Timetable;
import maite.maite.repository.UserRepository;
import maite.maite.repository.timetable.EventRepository;
import maite.maite.repository.timetable.TimetableRepository;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TimetableRepository timetableRepository;

    @Override
    public EventResponseDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        return convertToEventResponseDto(event);
    }

    @Override
    public List<EventResponseDto> getEventsByTimetable(Long timetableId) {
        List<Event> events = eventRepository.findByTimetableId(timetableId);

        return events.stream()
                .map(this::convertToEventResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponseDto createEvent(Long timetableId, EventRequestDto request) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        // 시간 충돌 검사
        checkTimeConflict(timetableId, request.getDay(), request.getStartTime(), request.getEndTime(), null);

        Event event = Event.builder()
                .title(request.getTitle())
                .day(request.getDay())
                .place(request.getPlace())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .timetable(timetable)
                .build();

        eventRepository.save(event);

        return convertToEventResponseDto(event);
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventRequestDto request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // 시간 충돌 검사
        checkTimeConflict(event.getTimetable().getId(), request.getDay(), request.getStartTime(), request.getEndTime(), eventId);

        event.setTitle(request.getTitle());
        event.setDay(request.getDay());
        event.setPlace(request.getPlace());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());

        return convertToEventResponseDto(event);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        eventRepository.delete(event);
    }

    private EventResponseDto convertToEventResponseDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .day(event.getDay())
                .place(event.getPlace())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }

    // 충돌 검사
    private void checkTimeConflict(Long timetableId, String day, String startTime, String endTime, Long excludeEventId) {
        List<Event> eventsOnSameDay = eventRepository.findByTimetableIdAndDay(timetableId, day);

        // excludeEventId가 null이 아니면(업데이트 시) 해당 이벤트 제외
        if (excludeEventId != null) {
            eventsOnSameDay = eventsOnSameDay.stream()
                    .filter(e -> !e.getId().equals(excludeEventId))
                    .collect(Collectors.toList());
        }

        // 시간충돌 검사
        for (Event existingEvent : eventsOnSameDay) {
            if (isTimeConflict(startTime, endTime, existingEvent.getStartTime(), existingEvent.getEndTime())) {
                throw new RuntimeException("해당 시간에 이미 다른 일정이 존재합니다.");
            }
        }
    }

    // 시간 충돌 확인 로직
    private boolean isTimeConflict(String newStart, String newEnd, String existingStart, String existingEnd) {
        int newStartMinutes = convertToMinutes(newStart);
        int newEndMinutes = convertToMinutes(newEnd);
        int existingStartMinutes = convertToMinutes(existingStart);
        int existingEndMinutes = convertToMinutes(existingEnd);

        return (newStartMinutes < existingEndMinutes && newEndMinutes > existingStartMinutes);
    }

    // 시간 변환 로직
    private int convertToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}
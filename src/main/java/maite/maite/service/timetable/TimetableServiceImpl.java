package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.timetable.Event;
import maite.maite.domain.entity.timetable.Timetable;
import maite.maite.repository.timetable.EventRepository;
import maite.maite.repository.timetable.TimetableRepository;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimetableServiceImpl implements TimetableService{

    private final TimetableRepository timetableRepository;
    private final EventRepository eventRepository;

    @Override
    public TimetableResponseDto getTimetable(Long timetableId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("시간표를 찾을 수 없습니다."));

        List<Event> events = eventRepository.findAllByTimetableId(timetableId);

        // 이벤트 DTO 리스트 생성
        List<EventRequestDto> eventDtos = events.stream()
                .map(event -> {
                    EventRequestDto dto = new EventRequestDto();
                    dto.setTitle(event.getTitle());
                    dto.setDay(event.getDay());
                    dto.setPlace(event.getPlace());
                    dto.setStartTime(event.getStartTime());
                    dto.setEndTime(event.getEndTime());
                    return dto;
                })
                .collect(Collectors.toList());

        //시간표 DTO 생성
        return TimetableResponseDto.builder()
                .id(timetable.getId())
                .userId(timetable.getUser().getId())
                .events(eventDtos)
                .build();
    }
}

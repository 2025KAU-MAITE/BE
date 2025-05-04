package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.timetable.Event;
import maite.maite.domain.entity.timetable.Timetable;
import maite.maite.repository.UserRepository;
import maite.maite.repository.timetable.EventRepository;
import maite.maite.repository.timetable.TimetableRepository;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;
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
    private final UserRepository userRepository;

    @Override
    public TimetableResponseDto getTimetable(Long timetableId, Long userId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("시간표를 찾을 수 없습니다."));

        if (!timetable.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        List<Event> events = eventRepository.findAllByTimetableId(timetableId);

        // 이벤트 DTO 리스트 생성
        List<EventResponseDto> eventDtos = events.stream()
                .map(event -> EventResponseDto.builder()
                        .id(event.getId())  // EventResponseDto에는 id 필드도 필요
                        .title(event.getTitle())
                        .day(event.getDay())
                        .place(event.getPlace())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .build())
                .collect(Collectors.toList());

        //시간표 DTO 생성
        return TimetableResponseDto.builder()
                .id(timetable.getId())
                .userId(userId)
                .events(eventDtos)
                .build();
    }

    @Override
    @Transactional
    public TimetableResponseDto createTimetable(TimetableRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Timetable timetable = Timetable.builder()
                .title(request.getTitle())
                .user(user)
                .build();

        timetableRepository.save(timetable);

        return TimetableResponseDto.builder()
                .id(timetable.getId())
                .userId(user.getId())
                .events(List.of())
                .build();
    }

    @Override
    @Transactional
    public void deleteTimetable(Long timetableId, Long userId) {

        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("시간표를 찾을 수 없습니다."));

        // Check if the timetable belongs to the authenticated user
        if (!timetable.getUser().getId().equals(userId)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        // 시간표에 연결된 모든 이벤트도 함께 삭제됨 (cascade 설정에 따라)
        timetableRepository.deleteById(timetableId);
    }
}

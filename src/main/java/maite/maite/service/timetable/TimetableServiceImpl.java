package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.MateStatus;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.timetable.Event;
import maite.maite.domain.entity.timetable.Timetable;
import maite.maite.repository.MateRepository;
import maite.maite.repository.UserRepository;
import maite.maite.repository.timetable.EventRepository;
import maite.maite.repository.timetable.TimetableRepository;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import maite.maite.web.dto.timetable.response.UserTimetableResponseDto;
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
    private final MateRepository mateRepository;

    @Override
    @Transactional(readOnly = true)
    public TimetableResponseDto getMyTimetable(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Timetable timetable = timetableRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("시간표가 존재하지 않습니다."));

        List<Event> events = eventRepository.findByTimetableId(timetable.getId());

        List<EventResponseDto> eventDtos = events.stream()
                .map(event -> EventResponseDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .day(event.getDay())
                        .place(event.getPlace())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .build())
                .collect(Collectors.toList());

        return TimetableResponseDto.builder()
                .id(timetable.getId())
                .userId(userId)
                .events(eventDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserTimetableResponseDto getTimetableByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Timetable timetable = timetableRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 시간표가 없습니다."));

        List<Event> events = eventRepository.findByTimetableId(timetable.getId());

        List<EventResponseDto> eventDtos = events.stream()
                .map(event -> EventResponseDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .day(event.getDay())
                        .place(event.getPlace())
                        .startTime(event.getStartTime())
                        .endTime(event.getEndTime())
                        .build())
                .collect(Collectors.toList());

        // 친구 수
        int mateCount = mateRepository.findAllByUserAndStatus(user, MateStatus.ACCEPTED).size();

        return UserTimetableResponseDto.builder()
                .timetableId(timetable.getId())
                .userId(user.getId())
                .userName(user.getName())
                .mateCount(mateCount)
                .events(eventDtos)
                .build();
    }

    @Override
    @Transactional
    public TimetableResponseDto createTimetable(TimetableRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (timetableRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("이미 시간표가 존재합니다. 한 사용자는 하나의 시간표만 가질 수 있습니다.");
        }

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
    public void deleteTimetable(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Timetable timetable = timetableRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 시간표가 없습니다."));

        // 첫 번째 시간표 삭제 (이벤트도 cascade로 함께 삭제됨)
        timetableRepository.delete(timetable);
    }
}

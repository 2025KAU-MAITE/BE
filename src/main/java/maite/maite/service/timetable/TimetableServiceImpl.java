package maite.maite.service.timetable;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
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
    @Transactional
    public List<TimetableResponseDto> getTimetablesByUserId(Long userId) {
        List<Timetable> timetables = timetableRepository.findByUserId(userId);

        return timetables.stream()
                .map(timetable -> {
                    List<Event> events = eventRepository.findAllByTimetableId(timetable.getId());

                    // 이벤트 DTO 리스트 생성
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

                    // 시간표 DTO 생성
                    return TimetableResponseDto.builder()
                            .id(timetable.getId())
                            .userId(timetable.getUser().getId())
                            .events(eventDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserTimetableResponseDto getTimetableByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Timetable> timetables = timetableRepository.findByUserId(user.getId());

        if (timetables.isEmpty()) {
            throw new IllegalArgumentException("해당 사용자의 시간표가 없습니다.");
        }

        Timetable timetable = timetables.get(0);
        List<Event> events = eventRepository.findAllByTimetableId(timetable.getId());

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

        //친구 수
        int mateCount = mateRepository.findAllByUser(user).size();

        return UserTimetableResponseDto.builder()
                .timetableId(timetable.getId())
                .userId(user.getId())
                .userName(user.getName())
                //.userEmail(user.getEmail())
                .mateCount(mateCount)
                .events(eventDtos)
                .build();
    }


    @Override
    public TimetableResponseDto getTimetable(Long timetableId, Long userId) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        if (!timetable.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("시간표를 찾을 수 없습니다."));

        // Check if the timetable belongs to the authenticated user
        if (!timetable.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        // 시간표에 연결된 모든 이벤트도 함께 삭제됨 (cascade 설정에 따라)
        timetableRepository.deleteById(timetableId);
    }
}

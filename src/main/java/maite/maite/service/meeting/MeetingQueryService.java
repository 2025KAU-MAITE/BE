package maite.maite.service.meeting;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.repository.meeting.MeetingRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingQueryService {
    private final MeetingRepository meetingRepository;

    public Meeting findMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("회의방을 찾을 수 없습니다."));
    }
}

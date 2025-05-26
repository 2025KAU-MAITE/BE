package maite.maite.service.meeting;

import lombok.*;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.room.UserRoom;
import maite.maite.repository.UserRepository;
import maite.maite.repository.meeting.MeetingRepository;
import maite.maite.repository.meeting.UserMeetingRepository;
import maite.maite.service.map.NaverMapService;
import maite.maite.service.meeting.MeetingInviteService;
import maite.maite.service.meeting.MeetingQueryService;
import maite.maite.service.meeting.MeetingService;
import maite.maite.service.room.RoomQueryService;
import maite.maite.web.dto.map.response.CafeResponse;
import maite.maite.web.dto.map.response.GeocodeResponse;
import maite.maite.web.dto.meeting.request.MeetingAddressRequest;
import maite.maite.web.dto.meeting.request.MeetingCreateRequest;
import maite.maite.web.dto.meeting.request.MeetingUpdateRequest;
import maite.maite.web.dto.meeting.response.MeetingResponse;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final UserMeetingRepository userMeetingRepository;

    private final MeetingQueryService meetingQueryService;
    private final RoomQueryService roomQueryService;
    private final MeetingInviteService meetingInviteService;
    private final NaverMapService naverMapService;

    // ✅ 1. 내가 속한 회의 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MeetingSummaryResponse> getMeetingsOfUser(User user) {
        return userMeetingRepository.findAllByUserAndStatus(user, InviteStatus.ACCEPTED)
                .stream()
                .map(UserMeeting::getMeeting)
                .map(meeting -> MeetingSummaryResponse.builder()
                        .meetingId(meeting.getId())
                        .title(meeting.getTitle())
                        .proposerName(meeting.getProposer().getName())
                        .meetingDate(meeting.getMeetingDate().toString())
                        .meetingTime(meeting.getMeetingTime().toString())
                        .address(meeting.getAddress())
                        .build()) // 필요한 필드만
                .collect(Collectors.toList());
    }

    // ✅ 2. 방 기준 회의 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MeetingSummaryResponse> getMeetingsByRoom(Long roomId, User user) {
        List<Meeting> meetings = meetingRepository.findAllByRoom_Id(roomId);

        return meetings.stream()
                .map(meeting -> {
                    // 회의와 유저로부터 UserMeeting 정보를 조회
                    Optional<UserMeeting> userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, user);

                    String acceptance = userMeeting
                            .map(um -> um.getStatus().name())
                            .orElse("NOT_INVITED");

                    return MeetingSummaryResponse.builder()
                            .meetingId(meeting.getId())
                            .title(meeting.getTitle())
                            .proposerName(meeting.getProposer().getName())
                            .meetingDate(meeting.getMeetingDate().toString())
                            .meetingTime(meeting.getMeetingTime().toString())
                            .address(meeting.getAddress())
                            .acceptance(acceptance)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ✅ 3. 회의 단건 조회
    @Override
    public MeetingResponse getMeetingDetail(Long meetingId) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);
        List<String> acceptedEmails = userMeetingRepository.findAllByMeetingAndStatus(meeting, InviteStatus.ACCEPTED)
                .stream()
                .map(ur -> ur.getUser().getEmail())
                .collect(Collectors.toList());

        return MeetingResponse.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .proposerName(meeting.getProposer().getName())
                .meetingDate(meeting.getMeetingDate().toString())
                .meetingTime(meeting.getMeetingTime().toString())
                .address(meeting.getAddress())
                .participantEmails(acceptedEmails)
                .record(meeting.getRecord())
                .recordText(meeting.getRecordText())
                .textSum(meeting.getTextSum())
                .createdAt(meeting.getCreatedAt())
                .build();
    }


    @Transactional
    @Override
    public void createMeeting(Long roomId, User proposer, MeetingCreateRequest req) {
        Meeting meeting = Meeting.builder()
                .room(roomQueryService.findRoomById(roomId))
                .title(req.getTitle())
                .proposer(proposer)
                .meetingDate(LocalDate.parse(req.getMeetingDate()))
                .meetingTime(LocalTime.parse(req.getMeetingTime()))
                .build();
        meeting = meetingRepository.save(meeting);

        // 호스트 자동 참가
        meetingInviteService.addHostAsParticipant(meeting, proposer);

        // 생성과 동시에 초대
        if (req.getInviteEmails() != null && !req.getInviteEmails().isEmpty()) {
            meetingInviteService.inviteUsers(meeting, req.getInviteEmails());
        }
    }

    // ✅ 5. 회의 수정
    @Override
    public void updateMeeting(Long meetingId, User user, MeetingUpdateRequest request) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);
        if (!meeting.getProposer().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한 없음");
        }
        if (request.getTitle() != null) {
            meeting.setTitle(request.getTitle());
        }
        if (request.getMeetingDay() != null) {
            meeting.setMeetingDate(LocalDate.parse(request.getMeetingDay()));
        }
        if (request.getMeetingTime() != null) {
            meeting.setMeetingTime(LocalTime.parse(request.getMeetingTime()));
        }
        if (request.getAddress() != null) {
            meeting.setAddress(request.getAddress());
        }
    }

    @Transactional
    public void leaveMeeting(Long meetingId, User user) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);
        if (meeting.getProposer().getId().equals(user.getId())) {
            throw new RuntimeException("제안자는 회의를 나갈 수 없습니다.");
        }

        UserMeeting userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new RuntimeException("참여 기록이 없습니다."));

        userMeeting.setStatus(InviteStatus.EXITED);
        userMeeting.setRespondedAt(LocalDateTime.now());
        userMeetingRepository.save(userMeeting);
    }

    @Transactional
    public void deleteMeeting(Long meetingId, User user) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);
        if (!meeting.getProposer().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한 없음");
        }
        userMeetingRepository.deleteAllByMeeting(meeting);
        meetingRepository.delete(meeting);
    }

    @Transactional
    public void saveParticipantAddress(Long meetingId, User user, MeetingAddressRequest address) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        UserMeeting userMeeting = userMeetingRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new RuntimeException("회의에 참여한 사용자만 주소를 등록할 수 있습니다."));

        userMeeting.setAddress(address.getAddress());
        //userMeeting.setRespondedAt(LocalDateTime.now()); // 응답 기록
        userMeetingRepository.save(userMeeting);
    }

    public List<CafeResponse> findMeetingNearbyCafes(Long meetingId) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        List<UserMeeting> participants = userMeetingRepository.findAllByMeetingAndStatus(
                meeting, InviteStatus.ACCEPTED
        );

        List<double[]> coordinates = new ArrayList<>();


        for (UserMeeting participant : participants) {
            String addr = participant.getAddress();
            if (addr != null && !addr.isBlank()) {
                try {
                    GeocodeResponse.Address geo = naverMapService.getCoordinatesFromAddress(addr);
                    coordinates.add(new double[]{Double.parseDouble(geo.getY()), Double.parseDouble(geo.getX())});
                } catch (Exception e) {
                    // 좌표 변환 실패한 주소는 제외
                }
            }
        }

        if (coordinates.isEmpty()) {
            throw new IllegalStateException("좌표를 얻을 수 있는 주소가 없습니다.");
        }

        double maxDistance = 0;
        for (int i = 0; i < coordinates.size(); i++) {
            for (int j = i + 1; j < coordinates.size(); j++) {
                double[] a = coordinates.get(i);
                double[] b = coordinates.get(j);
                double distance = naverMapService.calculateDistance(a[0], a[1], b[0], b[1]);
                maxDistance = Math.max(maxDistance, distance);
            }
        }
        if (maxDistance >= 100) {
            throw new IllegalStateException("참여자 간 거리가 100km 이상 떨어져 있어 중심지 계산이 불가능합니다.");
        }

        double avgLat = coordinates.stream().mapToDouble(coord -> coord[0]).average().orElseThrow();
        double avgLng = coordinates.stream().mapToDouble(coord -> coord[1]).average().orElseThrow();

        String locationName = naverMapService.getLocationNameFromCoordinates(avgLat, avgLng);

        return naverMapService.getNearbyCafes(locationName);
    }

    @Transactional
    public void setMeetingPlaceName(Long meetingId, User user, String address) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        // 제안자만 장소 설정 가능
        if (!meeting.getProposer().getId().equals(user.getId())) {
            throw new RuntimeException("회의 제안자만 장소를 설정할 수 있습니다.");
        }

        meeting.setAddress(address);
    }

}
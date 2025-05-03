package maite.maite.service.meeting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.domain.entity.room.Room;
import maite.maite.repository.UserRepository;
import maite.maite.repository.meeting.MeetingRepository;
import maite.maite.repository.meeting.UserMeetingRepository;
import maite.maite.repository.room.UserRoomRepository;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;
import maite.maite.web.dto.meeting.response.PendingMeetingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingInviteServiceImpl implements MeetingInviteService{

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final UserMeetingRepository userMeetingRepository;
    private final UserRoomRepository userRoomRepository;
    
    private final MeetingQueryService meetingQueryService;

    public void addHostAsParticipant(Meeting meeting, User proposer) {
        UserMeeting creator = UserMeeting.builder()
                .meeting(meeting)
                .user(proposer)
                .status(InviteStatus.ACCEPTED)
                .build();

        userMeetingRepository.save(creator);
    }

    @Transactional
    public void inviteUsers(Meeting meeting, List<String> inviteEmails) {
        for (String email : inviteEmails) {
            try {
                inviteUserToMeeting(meeting, meeting.getProposer(), email);
            } catch (RuntimeException e) {
                log.warn("초대 실패 ({}): {}", email, e.getMessage());
            }
        }
    }

    public void inviteUserToMeeting(Meeting meeting, User inviter, String inviteeEmail) {

        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + inviteeEmail));

        boolean alreadyJoined = userMeetingRepository.existsByMeetingAndUserAndStatus(meeting, invitee, InviteStatus.ACCEPTED);
        if (alreadyJoined) {
            throw new RuntimeException("이미 참여 중인 사용자입니다.");
        }

        boolean alreadyInvited = userMeetingRepository.existsByMeetingAndUserAndStatus(meeting, invitee, InviteStatus.PENDING);
        if (alreadyInvited) {
            throw new RuntimeException("이미 초대한 사용자입니다.");
        }

        UserMeeting invite = UserMeeting.builder()
                .meeting(meeting)
                .user(invitee)
                .status(InviteStatus.PENDING)
                .build();
        userMeetingRepository.save(invite);
    }

    public List<PendingMeetingResponse> getPendingInvitees(Long meetingId) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        List<UserMeeting> invites = userMeetingRepository.findAllByMeetingAndStatus(meeting, InviteStatus.PENDING);

        return invites.stream()
                .map(invite -> PendingMeetingResponse.builder()
                        .email(invite.getUser().getEmail())
                        .name(invite.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void acceptInvite(Long meetingId, User user) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        Room room = meeting.getRoom();
        boolean isInRoom = userRoomRepository.existsByRoomAndUserAndStatus(room, user, InviteStatus.ACCEPTED);
        if (!isInRoom) {
            throw new RuntimeException("회의방에 가입된 사용자만 회의에 참여할 수 있습니다.");
        }

        UserMeeting usermeeting = userMeetingRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new RuntimeException("초대 기록이 없습니다."));

        if (usermeeting.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("이미 처리된 초대입니다.");
        }

        usermeeting.setStatus(InviteStatus.ACCEPTED);
        usermeeting.setRespondedAt(LocalDateTime.now());
        userMeetingRepository.save(usermeeting);
    }

    @Transactional
    @Override
    public void rejectInvite(Long meetingId, User user) {
        Meeting meeting = meetingQueryService.findMeetingById(meetingId);

        UserMeeting usermeeting = userMeetingRepository.findByMeetingAndUser(meeting, user)
                .orElseThrow(() -> new RuntimeException("초대 기록이 없습니다."));

        if (usermeeting.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("이미 처리된 초대입니다.");
        }

        usermeeting.setStatus(InviteStatus.REJECTED);
        usermeeting.setRespondedAt(LocalDateTime.now());
        userMeetingRepository.save(usermeeting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingSummaryResponse> getPendingInvitations(User user) {
        return userMeetingRepository.findAllByUserAndStatus(user, InviteStatus.PENDING)
                .stream()
                .map(ur -> {
                    Meeting meeting = ur.getMeeting();
                    return MeetingSummaryResponse.builder()
                            .meetingId(meeting.getId())
                            .title(meeting.getTitle())
                            .proposerName(meeting.getProposer().getName())
                            .meetingDate(meeting.getMeetingDate().toString())
                            .meetingTime(meeting.getMeetingTime().toString())
                            .address(meeting.getAddress())
                            .build();
                })
                .toList();
    }
}
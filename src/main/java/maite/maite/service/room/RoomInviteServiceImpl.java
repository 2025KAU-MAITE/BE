package maite.maite.service.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.room.UserRoom;
import maite.maite.repository.room.RoomRepository;
import maite.maite.repository.room.UserRoomRepository;
import maite.maite.repository.UserRepository;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import maite.maite.web.dto.room.response.RoomSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomInviteServiceImpl implements RoomInviteService {
    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private final RoomQueryService roomQueryService;

    public void addHostAsParticipant(Room room, User host) {
        UserRoom creator = UserRoom.builder()
                .room(room)
                .user(host)
                .status(InviteStatus.ACCEPTED)
                .build();

        userRoomRepository.save(creator);
    }
    @Transactional
    public void inviteUsers(Room room, List<String> inviteEmails) {
        for (String email : inviteEmails) {
            try {
                inviteUserToRoom(room, room.getHost(), email);
            } catch (RuntimeException e) {
                log.warn("초대 실패 ({}): {}", email, e.getMessage());
            }
        }
    }

    public void inviteUserToRoom(Room room, User inviter, String inviteeEmail) {
        //Room room = roomQueryService.findRoomById(roomId);

        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("사용자 없음: " + inviteeEmail));

        boolean alreadyJoined = userRoomRepository.existsByRoomAndUserAndStatus(room, invitee, InviteStatus.ACCEPTED);
        if (alreadyJoined) {
            throw new RuntimeException("이미 참여 중인 사용자입니다.");
        }

        boolean alreadyInvited = userRoomRepository.existsByRoomAndUserAndStatus(room, invitee, InviteStatus.PENDING);
        if (alreadyInvited) {
            throw new RuntimeException("이미 초대한 사용자입니다.");
        }

        UserRoom invite = UserRoom.builder()
                .room(room)
                .user(invitee)
                .status(InviteStatus.PENDING)
                .build();
        userRoomRepository.save(invite);
    }

    public List<PendingRoomResponse> getPendingInvitees(Long roomId) {
        Room room = roomQueryService.findRoomById(roomId);

        List<UserRoom> invites = userRoomRepository.findAllByRoomAndStatus(room, InviteStatus.PENDING);

        return invites.stream()
                .map(invite -> PendingRoomResponse.builder()
                        .email(invite.getUser().getEmail())
                        .name(invite.getUser().getName())
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public void acceptInvite(Long roomId, User user) {
        Room room = roomQueryService.findRoomById(roomId);

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new RuntimeException("초대 기록이 없습니다."));

        if (userRoom.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("이미 처리된 초대입니다.");
        }

        userRoom.setStatus(InviteStatus.ACCEPTED);
        userRoom.setRespondedAt(LocalDateTime.now());
        userRoomRepository.save(userRoom);
    }

    @Transactional
    @Override
    public void rejectInvite(Long roomId, User user) {
        Room room = roomQueryService.findRoomById(roomId);

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new RuntimeException("초대 기록이 없습니다."));

        if (userRoom.getStatus() != InviteStatus.PENDING) {
            throw new RuntimeException("이미 처리된 초대입니다.");
        }

        userRoom.setStatus(InviteStatus.REJECTED);
        userRoom.setRespondedAt(LocalDateTime.now());
        userRoomRepository.save(userRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomSummaryResponse> getPendingInvitations(User user) {
        return userRoomRepository.findAllByUserAndStatus(user, InviteStatus.PENDING)
                .stream()
                .map(ur -> {
                    Room room = ur.getRoom();
                    return RoomSummaryResponse.builder()
                            .roomId(room.getId())
                            .name(room.getName())
                            .hostEmail(room.getHost().getEmail())
                            .description(room.getDescription())
                            .build();
                })
                .toList();
    }
}
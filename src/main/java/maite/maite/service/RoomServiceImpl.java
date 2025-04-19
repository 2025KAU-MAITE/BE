package maite.maite.service;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.Room;
import maite.maite.domain.entity.RoomInvite;
import maite.maite.domain.entity.User;
import maite.maite.repository.RoomInviteRepository;
import maite.maite.repository.RoomRepository;
import maite.maite.repository.UserRepository;
import maite.maite.web.dto.PendingRoomResponse;
import maite.maite.web.dto.RoomCreateRequest;
import maite.maite.web.dto.RoomResponse;
import maite.maite.web.dto.RoomUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl  implements RoomService{
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomInviteRepository roomInviteRepository;

    public List<RoomResponse> getRoomsOfUser(User user) {
        List<Room> rooms = roomRepository.findAllByParticipantsContaining(user);

        return rooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .createdAt(room.getCreatedAt())
                        .hostEmail(room.getHost().getEmail())
                        .participantEmails(
                                room.getParticipants().stream()
                                        .map(User::getEmail)
                                        .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("회의방을 찾을 수 없습니다."));

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .createdAt(room.getCreatedAt())
                .hostEmail(room.getHost().getEmail())
                .participantEmails(
                        room.getParticipants().stream()
                                .map(User::getEmail)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Transactional
    public void createRoom(User host, RoomCreateRequest request) {
        Room room = Room.builder()
                .name(request.getName())
                .host(host)
                .build();
        room.getParticipants().add(host); // 방 생성자도 자동으로 참가자에 포함
        roomRepository.save(room);
    }

    public void leaveRoom(Long roomId, User user) {
        Room room = findRoomById(roomId);
        if (room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("방장은 회의방을 나갈 수 없습니다.");
        }
        room.getParticipants().remove(user);
        roomRepository.save(room);
    }

    public void deleteRoom(Long roomId, User user) {
        Room room = findRoomById(roomId);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한 없음");
        }
        roomRepository.delete(room);
    }

    @Transactional
    public void updateRoom(Long roomId, User user, RoomUpdateRequest request) {
        Room room = findRoomById(roomId);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한 없음");
        }
        room.setName(request.getName());
        roomRepository.save(room);
    }

    public void inviteUserToRoom(Long roomId, User inviter, String inviteeEmail) {
        Room room = findRoomById(roomId);

        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (room.getParticipants().contains(invitee)) {
            throw new RuntimeException("이미 참여 중인 사용자입니다.");
        }

        boolean alreadyInvited = roomInviteRepository.existsByRoomAndInviteeAndStatus(room, invitee, InviteStatus.PENDING);
        if (alreadyInvited) {
            throw new RuntimeException("이미 초대한 사용자입니다. 수락 대기 중입니다.");
        }

        RoomInvite invite = RoomInvite.builder()
                .room(room)
                .inviter(inviter)
                .invitee(invitee)
                .build();
        roomInviteRepository.save(invite);
    }

    public List<PendingRoomResponse> getPendingInvitees(Long roomId) {
        Room room = findRoomById(roomId);

        List<RoomInvite> invites = roomInviteRepository.findAllByRoomAndStatus(room, InviteStatus.PENDING);

        return invites.stream()
                .map(invite -> PendingRoomResponse.builder()
                        .email(invite.getInvitee().getEmail())
                        .name(invite.getInvitee().getName())
                        .build())
                .collect(Collectors.toList());
    }


    // 내부 검색용
    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("회의방을 찾을 수 없습니다."));
    }
}

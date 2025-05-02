package maite.maite.service.room;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.room.UserRoom;
import maite.maite.repository.room.RoomRepository;
import maite.maite.repository.UserRepository;
import maite.maite.repository.room.UserRoomRepository;
import maite.maite.web.dto.room.request.RoomCreateRequest;
import maite.maite.web.dto.room.response.RoomResponse;
import maite.maite.web.dto.room.request.RoomUpdateRequest;
import maite.maite.web.dto.room.response.RoomSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final RoomQueryService roomQueryService;
    private final RoomInviteService roomInviteService;

    @Override
    public List<RoomSummaryResponse> getRoomsOfUser(User user) {
        return userRoomRepository.findAllByUserAndStatus(user, InviteStatus.ACCEPTED)
                .stream()
                .map(UserRoom::getRoom)
                .map(room -> RoomSummaryResponse.builder()
                        .name(room.getName())
                        .hostEmail(room.getHost().getEmail())
                        .description(room.getDescription())
                        .build()) // 필요한 필드만
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomDetail(Long roomId) {
        Room room = roomQueryService.findRoomById(roomId);
        List<String> acceptedEmails = userRoomRepository.findAllByRoomAndStatus(room, InviteStatus.ACCEPTED)
                .stream()
                .map(ur -> ur.getUser().getEmail())
                .collect(Collectors.toList());

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .description(room.getDescription())
                .createdAt(room.getCreatedAt())
                .hostEmail(room.getHost().getEmail())
                .participantEmails(acceptedEmails)
                .build();
    }

    @Transactional
    @Override
    public void createRoom(User host, RoomCreateRequest req) {
        Room room = Room.builder()
                .name(req.getName())
                .description(req.getDescription())
                .host(host)
                .build();
        room = roomRepository.save(room);

        // 호스트 자동 참가
        roomInviteService.addHostAsParticipant(room, host);

        // 생성과 동시에 초대
        if (req.getInviteEmails() != null && !req.getInviteEmails().isEmpty()) {
            roomInviteService.inviteUsers(room, req.getInviteEmails());
        }
    }

    public void leaveRoom(Long roomId, User user) {
        Room room = roomQueryService.findRoomById(roomId);
        if (room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("방장은 회의방을 나갈 수 없습니다.");
        }

        UserRoom userRoom = userRoomRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new RuntimeException("참여 기록이 없습니다."));

        userRoom.setStatus(InviteStatus.EXITED);
        userRoom.setRespondedAt(LocalDateTime.now());
        userRoomRepository.save(userRoom);
    }

    public void deleteRoom(Long roomId, User user) {
        Room room = roomQueryService.findRoomById(roomId);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한 없음");
        }
        roomRepository.delete(room);
    }

    @Transactional
    public void updateRoom(Long roomId, User user, RoomUpdateRequest request) {
        Room room = roomQueryService.findRoomById(roomId);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한 없음");
        }
        room.setName(request.getName());
        roomRepository.save(room);
    }


}

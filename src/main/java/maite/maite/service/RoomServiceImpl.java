package maite.maite.service;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.Room;
import maite.maite.domain.entity.User;
import maite.maite.repository.RoomRepository;
import maite.maite.web.dto.RoomCreateRequest;
import maite.maite.web.dto.RoomResponse;
import maite.maite.web.dto.RoomUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl  implements RoomService{
    private final RoomRepository roomRepository;

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

    public Room createRoom(User host, RoomCreateRequest request) {
        Room room = Room.builder()
                .name(request.getName())
                .host(host)
                .build();
        room.getParticipants().add(host); // 방 생성자도 자동으로 참가자에 포함
        return roomRepository.save(room);
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

    public RoomResponse updateRoom(Long roomId, User user, RoomUpdateRequest request) {
        Room room = findRoomById(roomId);
        if (!room.getHost().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한 없음");
        }
        room.setName(request.getName());
        Room updated = roomRepository.save(room);

        return RoomResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .createdAt(updated.getCreatedAt())
                .hostEmail(updated.getHost().getEmail())
                .participantEmails(
                        updated.getParticipants().stream()
                                .map(User::getEmail)
                                .collect(Collectors.toList())
                )
                .build();
    }

    // 내부 검색용
    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("회의방을 찾을 수 없습니다."));
    }
}

package maite.maite.service.room;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.room.Room;
import maite.maite.repository.room.RoomRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomQueryService {
    private final RoomRepository roomRepository;

    public Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("회의방을 찾을 수 없습니다."));
    }
}


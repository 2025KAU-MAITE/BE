package maite.maite.service.room;

import java.util.List;

import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import maite.maite.web.dto.room.request.RoomCreateRequest;
import maite.maite.web.dto.room.response.RoomResponse;
import maite.maite.web.dto.room.request.RoomUpdateRequest;
import maite.maite.web.dto.room.response.RoomSummaryResponse;

public interface RoomService {
    List<RoomSummaryResponse> getRoomsOfUser(User user);
    RoomResponse getRoomDetail(Long roomId);
    void createRoom(User host, RoomCreateRequest request);
    void leaveRoom(Long roomId, User user);
    void deleteRoom(Long roomId, User user); // host만
    void updateRoom(Long roomId, User user, RoomUpdateRequest request); // host만
}

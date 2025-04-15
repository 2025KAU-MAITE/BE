package maite.maite.service;

import java.util.List;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.Room;
import maite.maite.web.dto.RoomCreateRequest;
import maite.maite.web.dto.RoomResponse;
import maite.maite.web.dto.RoomUpdateRequest;

public interface RoomService {
    List<RoomResponse> getRoomsOfUser(User user);
    RoomResponse getRoomDetail(Long roomId);
    Room createRoom(User host, RoomCreateRequest request);
    void leaveRoom(Long roomId, User user);
    void deleteRoom(Long roomId, User user); // host만
    RoomResponse updateRoom(Long roomId, User user, RoomUpdateRequest request); // host만
}
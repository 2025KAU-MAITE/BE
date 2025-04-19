package maite.maite.service;

import java.util.List;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.Room;
import maite.maite.web.dto.PendingRoomResponse;
import maite.maite.web.dto.RoomCreateRequest;
import maite.maite.web.dto.RoomResponse;
import maite.maite.web.dto.RoomUpdateRequest;

public interface RoomService {
    List<RoomResponse> getRoomsOfUser(User user);
    RoomResponse getRoomDetail(Long roomId);
    void createRoom(User host, RoomCreateRequest request);
    void leaveRoom(Long roomId, User user);
    void deleteRoom(Long roomId, User user); // host만
    void updateRoom(Long roomId, User user, RoomUpdateRequest request); // host만
    void inviteUserToRoom(Long roomId, User inviter, String inviteeEmail);
    List<PendingRoomResponse> getPendingInvitees(Long roomId);
}
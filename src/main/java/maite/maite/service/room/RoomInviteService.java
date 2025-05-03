package maite.maite.service.room;

import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import maite.maite.web.dto.room.response.RoomSummaryResponse;

import java.util.List;

public interface RoomInviteService {
    void addHostAsParticipant(Room room, User host);
    void inviteUsers(Room room, List<String> inviteEmails);
    void inviteUserToRoom(Room room, User inviter, String inviteeEmail);
    List<PendingRoomResponse> getPendingInvitees(Long roomId);
    void acceptInvite(Long roomId, User user);
    void rejectInvite(Long roomId, User user);
    List<RoomSummaryResponse> getPendingInvitations(User user);
}

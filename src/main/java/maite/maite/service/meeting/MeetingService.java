package maite.maite.service.meeting;

import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.domain.entity.room.Room;
import maite.maite.web.dto.map.response.CafeResponse;
import maite.maite.web.dto.meeting.request.MeetingAddressRequest;
import maite.maite.web.dto.meeting.request.MeetingCreateRequest;
import maite.maite.web.dto.meeting.request.MeetingUpdateRequest;
import maite.maite.web.dto.meeting.response.MeetingCreateResponse;
import maite.maite.web.dto.meeting.response.MeetingResponse;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;

import java.util.List;

public interface MeetingService {
    MeetingResponse getMeetingDetail(Long meetingId);
    List<MeetingSummaryResponse> getMeetingsOfUser(User user);
    List<MeetingSummaryResponse> getMeetingsByRoom(Long roomId, User user);
    MeetingCreateResponse createMeeting(Long roomId, User proposer, MeetingCreateRequest request);
    void updateMeeting(Long meetingId, User user, MeetingUpdateRequest request);
    void deleteMeeting(Long meetingId, User user);
    void leaveMeeting(Long meetingId, User user);
    void saveParticipantAddress(Long meetingId, User user, MeetingAddressRequest address);
    List<CafeResponse> findMeetingNearbyCafes(Long meetingId);
    void setMeetingPlaceName(Long meetingId, User user, String address);
}
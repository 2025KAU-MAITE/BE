package maite.maite.service.meeting;

import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.web.dto.meeting.response.PendingMeetingResponse;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;

import java.util.List;

public interface MeetingInviteService {
    void addHostAsParticipant(Meeting meeting, User host);
    void inviteUsers(Meeting meeting, List<String> inviteEmails);
    void inviteUserToMeeting(Meeting meeting, User inviter, String inviteeEmail);
    List<PendingMeetingResponse> getPendingInvitees(Long meetingId);
    void acceptInvite(Long meetingId, User user);
    void rejectInvite(Long meetingId, User user);
    List<MeetingSummaryResponse> getPendingInvitations(User user);
}

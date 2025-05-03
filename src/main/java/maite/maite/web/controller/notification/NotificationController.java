package maite.maite.web.controller.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.meeting.MeetingInviteService;
import maite.maite.service.room.RoomInviteService;
//import maite.maite.service.meeting.MeetingInviteService;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;
import maite.maite.web.dto.room.response.PendingRoomResponse;
//import maite.maite.web.dto.meeting.response.PendingMeetingResponse;
import maite.maite.web.dto.room.response.RoomSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "notification", description = "알림 관련 API")
@RequiredArgsConstructor
public class NotificationController {

    private final RoomInviteService roomInviteService;
    private final MeetingInviteService meetingInviteService;

    @Operation(summary = "내 회의방 초대 알림 조회")
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomSummaryResponse>> getRoomInvitations(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        List<RoomSummaryResponse> invitations = roomInviteService.getPendingInvitations(userDetails.getUser());
        return ResponseEntity.ok(invitations);
    }

    @Operation(summary = "내 회의 초대 알림 조회")
    @GetMapping("/meetings")
    public ResponseEntity<List<MeetingSummaryResponse>> getMeetingInvitations(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        List<MeetingSummaryResponse> invitations = meetingInviteService.getPendingInvitations(userDetails.getUser());
        return ResponseEntity.ok(invitations);
    }
}

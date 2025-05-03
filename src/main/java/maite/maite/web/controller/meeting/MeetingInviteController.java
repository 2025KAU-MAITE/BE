package maite.maite.web.controller.meeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.meeting.MeetingInviteService;
import maite.maite.service.meeting.MeetingQueryService;
import maite.maite.web.dto.meeting.request.MeetingInviteRequest;
import maite.maite.web.dto.meeting.response.PendingMeetingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings/{meetingId}/invites")
@Tag(name = "meetingInvite", description = "회의 초대 API")
@RequiredArgsConstructor
public class MeetingInviteController {

    private final MeetingInviteService meetingInviteService;
    private final MeetingQueryService meetingQueryService;
    @Operation(summary = "초대")
    @PostMapping("/")
    public ResponseEntity<?> inviteUserTomeeting(@PathVariable Long meetingId,
                                              @RequestBody MeetingInviteRequest request,
                                              @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Meeting meeting = meetingQueryService.findMeetingById(meetingId);
        meetingInviteService.inviteUserToMeeting(meeting, userDetails.getUser(), request.getEmail());
        return ResponseEntity.ok("사용자 초대 완료");
    }

    @Operation(summary = "초대(보류)중인 사람 목록 조회")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingInvitees(@PathVariable Long meetingId) {
        List<PendingMeetingResponse> pending = meetingInviteService.getPendingInvitees(meetingId);
        return ResponseEntity.ok(pending);
    }

    @Operation(summary = "초대 수락 ")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvite(@PathVariable Long meetingId,
                                          @AuthenticationPrincipal CustomerUserDetails userDetails) {
        meetingInviteService.acceptInvite(meetingId, userDetails.getUser());
        return ResponseEntity.ok("초대 수락 완료");
    }

    @Operation(summary = "초대 거절")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectInvite(@PathVariable Long meetingId,
                                          @AuthenticationPrincipal CustomerUserDetails userDetails) {
        meetingInviteService.rejectInvite(meetingId, userDetails.getUser());
        return ResponseEntity.ok("초대 거절 완료");
    }
}

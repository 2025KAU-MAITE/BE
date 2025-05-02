package maite.maite.web.controller.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.room.Room;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.room.RoomInviteService;
import maite.maite.service.room.RoomQueryService;
import maite.maite.web.dto.room.request.RoomInviteRequest;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms/{roomId}/invites")
@Tag(name = "roomInvite", description = "회의방 초대 API")
@RequiredArgsConstructor
public class RoomInviteController {

    private final RoomInviteService roomInviteService;
    private final RoomQueryService roomQueryService;
    @Operation(summary = "초대")
    @PostMapping("/")
    public ResponseEntity<?> inviteUserToRoom(@PathVariable Long roomId,
                                              @RequestBody RoomInviteRequest request,
                                              @AuthenticationPrincipal CustomerUserDetails userDetails) {

        Room room = roomQueryService.findRoomById(roomId);
        roomInviteService.inviteUserToRoom(room, userDetails.getUser(), request.getEmail());
        return ResponseEntity.ok("사용자 초대 완료");
    }

    @Operation(summary = "초대(보류)중인 사람 목록 조회")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingInvitees(@PathVariable Long roomId) {
        List<PendingRoomResponse> pending = roomInviteService.getPendingInvitees(roomId);
        return ResponseEntity.ok(pending);
    }

    @Operation(summary = "초대 수락 ")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptInvite(@PathVariable Long roomId,
                                          @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomInviteService.acceptInvite(roomId, userDetails.getUser());
        return ResponseEntity.ok("초대 수락 완료");
    }

    @Operation(summary = "초대 거절")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectInvite(@PathVariable Long roomId,
                                          @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomInviteService.rejectInvite(roomId, userDetails.getUser());
        return ResponseEntity.ok("초대 거절 완료");
    }
}

package maite.maite.web.controller.room;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.room.RoomService;
import maite.maite.service.room.RoomInviteService;
import maite.maite.web.dto.room.request.RoomCreateRequest;
import maite.maite.web.dto.room.request.RoomInviteRequest;
import maite.maite.web.dto.room.request.RoomUpdateRequest;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import maite.maite.web.dto.room.response.RoomResponse;
import maite.maite.web.dto.room.response.RoomSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Tag(name = "room", description = "회의방 API")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomInviteService roomInviteService;

    @Operation(summary = "내가 속한 회의방 목록 조회")
    @GetMapping
    public List<RoomSummaryResponse> getMyRooms(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        return roomService.getRoomsOfUser(userDetails.getUser());
    }

    @Operation(summary = "회의방 상세 조회")
    @GetMapping("/{roomId}")
    public RoomResponse getRoomDetail(@PathVariable Long roomId) {
        return roomService.getRoomDetail(roomId);
    }

    @Operation(summary = "회의방 생성")
    @PostMapping
    public ResponseEntity<Void> createRoom(
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody RoomCreateRequest request
    ) {
        roomService.createRoom(userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회의방 수정")
    @PutMapping("/{roomId}")
    public ResponseEntity<Void> updateRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody RoomUpdateRequest request
    ) {
        roomService.updateRoom(roomId, userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회의방 삭제")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        roomService.deleteRoom(roomId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회의방 나가기")
    @DeleteMapping("/{roomId}/me")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        roomService.leaveRoom(roomId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
package maite.maite.web.controller.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.security.CustomerUserDetails;
//import maite.maite.service.S3Service;
import maite.maite.service.S3Service;
import maite.maite.web.dto.chat.request.MessageRequestDto;
import maite.maite.web.dto.chat.response.ImageUploadResponseDto;
import maite.maite.web.dto.chat.response.MessageResponseDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import maite.maite.service.chat.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final S3Service s3Service;

    //메세지 전송 (WebSocket)
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload MessageRequestDto messageRequest
            //@AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        //Long userId = userDetails.getUser().getId();
        MessageResponseDto message = chatService.sendTextMessage(
                roomId,
                messageRequest.getSenderId(),
                messageRequest.getContent()
        );

        //해당 채팅방을 구독중인 모든 클라이언트에게 메시지 전달
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    //이미지 업로드 API
    @Operation(summary = "채팅방 이미지 업로드 API")
    @PostMapping(value = "{roomId}/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ApiResponse<ImageUploadResponseDto> upLoadImage(
            @PathVariable Long roomId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomerUserDetails userDetails
            ){
        Long userId = userDetails.getUser().getId();

        //S3 이미지 업로드
        String imageUrl = s3Service.uploadChatImage(file, roomId);

        MessageResponseDto message = chatService.sendImageMessage(
                roomId,
                userId,
                imageUrl
        );

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);

        return ApiResponse.onSuccess(new ImageUploadResponseDto(imageUrl));
    }

    //채팅방 입장 (WebSocket)


    //채팅 내역 조회
    @Operation(summary = "채팅 내역 조회 API", description = "특정 채팅방의 메시지 내역을 조회합니다")
    @GetMapping("/{roomId}/messages")
    public ApiResponse<List<MessageResponseDto>> getChatMessages(
            @PathVariable Long roomId,
            @Parameter(description = "마지막으로 받은 메시지의  ID (이 ID보다 이전 메시지를 가져옴)")
            @RequestParam(required = false) Long lastMessageId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<MessageResponseDto> messages = chatService.getChatMessages(roomId, userId, lastMessageId);
        return ApiResponse.onSuccess(messages);
    }

    //websocket을 통한 읽음 상태 업데이트
    @MessageMapping("/chat.markAsRead/{roomId}")
    public void markAsReadWebSocket(
            @DestinationVariable Long roomId,
            @Payload Map<String, Object> payload
            //@AuthenticationPrincipal CustomerUserDetails userDetails
            ){
        //Long userId = userDetails.getUser().getId();
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long messageId = Long.valueOf(payload.get("messageId").toString());

        chatService.markMessageAsRead(roomId, userId , messageId);
    }
}

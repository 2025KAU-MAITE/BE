package maite.maite.web.controller.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.security.CustomerUserDetails;
//import maite.maite.service.S3Service;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    //private final S3Service s3Service;

    //메세지 전송 (WebSocket)
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload MessageRequestDto messageRequest
    ) {
        MessageResponseDto message = chatService.sendTextMessage(
                roomId,
                messageRequest.getSenderId(),
                messageRequest.getContent()
        );

        //해당 채팅방을 구독중인 모든 클라이언트에게 메시지 전달
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    //이미지 업로드 API
//    @Operation(summary = "채팅방 이미지 업로드 API")
//    @PostMapping("{roomId}/images")
//    public ApiResponse<ImageUploadResponseDto> upLoadImage(
//            @PathVariable Long roomId,
//            @RequestParam("file") MultipartFile file,
//            @AuthenticationPrincipal CustomerUserDetails userDetails
//            ){
//        Long userId = userDetails.getUser().getId();
//
//        //S3 이미지 업로드
//        String imageUrl = s3Service.uploadChatImage(file, roomId);
//
//        MessageResponseDto message = chatService.sendImageMessage(
//                roomId,
//                userId,
//                imageUrl
//        );
//
//        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
//
//        return ApiResponse.onSuccess(new ImageUploadResponseDto(imageUrl));
//    }

    //채팅방 입장 (WebSocket)


    //채팅 내역 조회

}

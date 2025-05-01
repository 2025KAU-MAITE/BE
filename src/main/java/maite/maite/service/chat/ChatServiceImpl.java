package maite.maite.service.chat;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.ChatRoomUserRole;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.chat.ChatRoom;
import maite.maite.domain.entity.chat.Message;
import maite.maite.domain.mapping.ChatRoomUser;
import maite.maite.repository.UserRepository;
import maite.maite.repository.chat.ChatRoomRepository;
import maite.maite.repository.chat.ChatRoomUserRepository;
import maite.maite.repository.chat.MessageRepository;
import maite.maite.web.dto.chat.response.ChatRoomResponseDto;
import maite.maite.web.dto.chat.response.MessageResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;

    // 개인 채팅방 생성
    @Override
    @Transactional
    public ChatRoomResponseDto createPersonalChatRoom(Long userId, Long receiverId) {
        // 이미 존재하는 개인 채팅방인지 확인
        List<ChatRoom> existingRooms = chatRoomRepository.findChatRoomsByUserIds(userId, receiverId, false);
        if (!existingRooms.isEmpty()) {
            ChatRoom existingRoom = existingRooms.get(0);
            return new ChatRoomResponseDto(
                    existingRoom.getId(),
                    userId,
                    List.of(receiverId),
                    existingRoom.getRoomName(),
                    existingRoom.isGroupChat(),
                    existingRoom.getLastMessageContent(),
                    existingRoom.getLastMessageTime(),
                    existingRoom.getProfileImageUrl(),
                    existingRoom.getChatRoomUsers().size()
            );
        }

        // 새로운 개인 채팅방 생성
        User sender = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String roomName = receiver.getName(); //채팅방 이름은 상대방 이름

        ChatRoom chatRoom = ChatRoom.createPersonalChatRoom(roomName);
        chatRoomRepository.save(chatRoom); // 디비 저장

        //사용자 추가
        ChatRoomUser senderMember = ChatRoomUser.builder()
                .user(sender)
                .chatRoom(chatRoom)
                //.role(ChatRoomUserRole.User) 권한을 넣을까 말까
                .build();

        ChatRoomUser receiverMember = ChatRoomUser.builder()
                .user(receiver)
                .chatRoom(chatRoom)
                //.role(ChatRoomUserRole.User) 권한을 넣을까 말까
                .build();

        chatRoom.addUser(senderMember);
        chatRoom.addUser(receiverMember);

        chatRoomUserRepository.save(senderMember);
        chatRoomUserRepository.save(receiverMember);

        return new ChatRoomResponseDto(
                chatRoom.getId(),
                userId,
                List.of(receiverId),
                chatRoom.getRoomName(),
                false,
                null,
                null,
                null,//receiver.getProfileImageUrl(), //상대방 프로필 이미지로 하려했지만 아직 s3 미연결
                2
        );
    }

    // 그룹 채팅방 생성
    @Override
    @Transactional
    public ChatRoomResponseDto createGroupChatRoom(String roomName, Long hostUserId, List<Long> MemberIds) {
        User host = userRepository.findById(hostUserId)
                .orElseThrow(() -> new IllegalArgumentException("호스트를 찾을 수 없습니다."));

        ChatRoom chatRoom = ChatRoom.createGroupChatRoom(roomName, null);
        chatRoomRepository.save(chatRoom);

        //방장 추가
        ChatRoomUser hostMember = ChatRoomUser.builder()
                .user(host)
                .chatRoom(chatRoom)
                .role(ChatRoomUserRole.ADMIN) //권한을 넣을까 말까
                .build();

        chatRoom.addUser(hostMember);
        chatRoomUserRepository.save(hostMember);

        //참가자 추가
        int participantCount = 1; // 방장 포함
        for (Long memberId : MemberIds) {
            if(!memberId.equals(hostUserId)) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(()-> new IllegalArgumentException("참가자를 찾을 수 없습니다."));
                ChatRoomUser memberUser = ChatRoomUser.builder()
                        .user(member)
                        .chatRoom(chatRoom)
                        .role(ChatRoomUserRole.USER) //권한을 넣을까 말까
                        .build();

                chatRoom.addUser(memberUser);
                chatRoomUserRepository.save(memberUser);
                participantCount++;
            }
        }

        return new ChatRoomResponseDto(
                chatRoom.getId(),
                hostUserId,
                MemberIds,
                chatRoom.getRoomName(),
                true,
                null,
                null,
                chatRoom.getProfileImageUrl(),
                participantCount
        );
    }

    // 채팅방 목록 조회
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRoomsByUserId(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserId(userId);
        List<ChatRoomResponseDto> result = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            result.add(new ChatRoomResponseDto(
                    chatRoom.getId(),
                    null,
                    null,
                    //userId,
                    //chatRoom.getChatRoomUsers().stream()
                            //.map(chatRoomUser -> chatRoomUser.getUser().getId())
                            //.toList(),
                    chatRoom.getRoomName(),
                    chatRoom.isGroupChat(),
                    chatRoom.getLastMessageContent(),
                    chatRoom.getLastMessageTime(),
                    chatRoom.getProfileImageUrl(),
                    chatRoom.getChatRoomUsers().size()
            ));
        }
        return result;
    }

    // 채팅방 나가기
    @Override
    @Transactional
    public void leaveChatRoom(Long userId, Long roomId) {
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(userId, roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 사용자를 찾을 수 없습니다."));

        ChatRoom chatRoom = chatRoomUser.getChatRoom();

        // 그룹 채팅방일 경우
        if (chatRoom.isGroupChat()) {
            // 관리자인지 확인
            if (chatRoomUser.getRole() == ChatRoomUserRole.ADMIN) {
                // 다른 관리자가 있는지 확인
                boolean hasOtherAdmin = chatRoomUserRepository.existsByChatRoomIdAndRoleAndUserIdNot(
                        roomId, ChatRoomUserRole.ADMIN, userId);

                if (!hasOtherAdmin) {
                    // 다른 관리자가 없다면 다른 멤버를 관리자로 승격
                    ChatRoomUser newAdmin = chatRoomUserRepository.findFirstByChatRoomIdAndUserIdNot(roomId, userId)
                            .orElse(null);

                    if (newAdmin != null) {
                        newAdmin.setRole(ChatRoomUserRole.ADMIN);
                        chatRoomUserRepository.save(newAdmin);
                    } else {
                        // 다른 멤버가 없으면 채팅방 삭제
                        chatRoomRepository.delete(chatRoom);
                        return;
                    }
                }
            }
        }

        chatRoomUserRepository.delete(chatRoomUser);
    }

    // 채팅방 삭제
    @Override
    @Transactional
    public void deleteChatRoom(Long userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        if (chatRoom.isGroupChat()) {
            ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(userId, roomId)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방 사용자를 찾을 수 없습니다."));
            if (chatRoomUser.getRole() != ChatRoomUserRole.ADMIN) {
                throw new IllegalArgumentException("관리자만 채팅방을 삭제할 수 있습니다.");
            }
        }

        // 채팅방에 속한 모든 사용자를 삭제
        chatRoomUserRepository.deleteAllByChatRoomId(roomId);
        // 메시지 삭제
        //messageRepository.deleteAllByChatRoomId(roomId);
        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }

    //채팅방 초대
    @Override
    @Transactional
    public void inviteUserChatRoom( Long roomId, Long inviterId, List<Long> userIds) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        //초대자가 채팅방에 속해있는지 확인
        ChatRoomUser inviter = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, inviterId)
                .orElseThrow(()-> new IllegalArgumentException("채팅방에 참여하지 않은 유저입니다."));

        //그룹채팅방이 아니면 초대 불가
        if (!chatRoom.isGroupChat()) {
            throw new IllegalArgumentException("개인 채팅방에는 사용자를 초대할 수 없습니다.");
        }

        //사용자 초대 로직
        for(Long newUserId : userIds) {
            boolean alreadyExists = chatRoomUserRepository.existsByChatRoomIdAndUserId(roomId, newUserId);
            if (alreadyExists) {
                continue;
            }

            User newUser = userRepository.findById(newUserId).
                    orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            ChatRoomUser newMember = ChatRoomUser.builder()
                    .user(newUser)
                    .chatRoom(chatRoom)
                    .role(ChatRoomUserRole.USER)
                    .build();

            chatRoom.addUser(newMember);
            chatRoomUserRepository.save(newMember);
        }
    }

    @Override
    @Transactional
    public MessageResponseDto sendTextMessage(Long roomId, Long senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 비어있습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다."));

        Message message = Message.builder()
                .content(content)
                //.imageUrl(null) // 이미지 URL은 null로 설정
                .sendAt(LocalDateTime.now())
                .chatRoom(chatRoom)
                .sender(sender)
                .build();

        messageRepository.save(message);

        chatRoom.addMessage(message);
        chatRoomRepository.save(chatRoom);

        return MessageResponseDto.builder()
                .id(message.getId())
                .roomId(roomId)
                .senderId(senderId)
                .senderName(sender.getName())
                //.senderProfileImageUrl(sender.getProfileImageUrl()) 유저이미지 아직 설정 안됨
                .content(message.getContent())
                .imageUrl(null)
                .sendAt(message.getSendAt())
                .isRead(false)
                .build();
    }

    @Override
    @Transactional
    public MessageResponseDto sendImageMessage(Long roomId, Long senderId, String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("이미지 URL이 필요합니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자가 채팅방에 속해있는지 확인
        chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, senderId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방에 참여하지 않은 사용자입니다."));

        // 메시지 생성 (이미지만 있는 경우)
        Message message = Message.builder()
                .content(null)
                //.imageUrl(imageUrl)
                .sender(sender)
                .chatRoom(chatRoom)
                .sendAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        // 채팅방 최신 메시지 정보 업데이트
        chatRoom.updateLastMessage(message);
        chatRoomRepository.save(chatRoom);

        return MessageResponseDto.builder()
                .id(message.getId())
                .roomId(roomId)
                .senderId(senderId)
                .senderName(sender.getName())
                //.senderProfileImageUrl(sender.getProfileImageUrl())
                .content(null)
                .imageUrl(message.getImageUrl())
                .sendAt(message.getSendAt())
                .isRead(false)
                .build();
    }
}

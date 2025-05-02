package maite.maite.domain.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.BaseEntity;
import maite.maite.domain.mapping.ChatRoomUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String roomName;

    // 그룹 채팅방 여부
    @Column(nullable = false)
    @Builder.Default
    private boolean isGroupChat = false;

    // 그룹 채팅방 프로필 이미지 URL (선택 사항)
    private String profileImageUrl;

    // 최대 참여 인원 (그룹 채팅방에만 해당, null이면 제한 없음)
    private Integer maxMembers;

    // 마지막 메시지 내용 (채팅방 목록에서 미리보기 용도)
    private String lastMessageContent;

    // 마지막 메시지 시간
    private LocalDateTime lastMessageTime;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    // 채팅방 참여자 추가 헬퍼 메소드
    public void addUser(ChatRoomUser chatRoomUser) {
        this.chatRoomUsers.add(chatRoomUser);
        chatRoomUser.setChatRoom(this);
    }

    // 메시지 추가 헬퍼 메소드
    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatRoom(this);

        // 마지막 메시지 정보 업데이트
        this.lastMessageContent = message.getContent();
        this.lastMessageTime = message.getSendAt();
    }

    // 마지막 메시지 정보 업데이트 메소드 추가
    public void updateLastMessage(Message message) {
        this.lastMessageContent = message.getImageUrl() != null && message.getImageUrl().trim().length() > 0
                ? "이미지를 보냈습니다."
                : message.getContent();
        this.lastMessageTime = message.getSendAt();
    }

    // 그룹 채팅방 생성 팩토리 메소드
    public static ChatRoom createGroupChatRoom(String roomName, String description) {
        return ChatRoom.builder()
                .roomName(roomName)
                .isGroupChat(true)
                //.description(description)
                .build();
    }

    // 개인 채팅방 생성 팩토리 메소드
    public static ChatRoom createPersonalChatRoom(String roomName) {
        return ChatRoom.builder()
                .roomName(roomName)
                .isGroupChat(false)
                .build();
    }
}

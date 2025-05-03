package maite.maite.domain.entity.meeting;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

import maite.maite.domain.entity.User;
import maite.maite.domain.entity.room.Room;

@Entity
@Table(name = "meeting")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회의방 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate meetingDate;

    @Column(nullable = false)
    private LocalTime meetingTime;

    @Column(nullable = false)
    private String address;

    // 녹음 파일 정보 (경로 등)
    private String record;

    @Lob
    private String recordText;

    @Lob
    private String textSum;

    // 회의 생성자 정보도 필요하다면 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposer_id")
    private User proposer;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}

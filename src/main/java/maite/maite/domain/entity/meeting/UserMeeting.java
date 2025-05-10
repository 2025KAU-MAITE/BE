package maite.maite.domain.entity.meeting;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@IdClass(UserMeetingId.class)
@Table(name = "user_meeting")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMeeting {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status; // PENDING, ACCEPTED, REJECTED, EXITED

    private String address;

    @Column(nullable = false)
    private LocalDateTime invitedAt;

    @Column
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        if (this.invitedAt == null) {
            this.invitedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = InviteStatus.PENDING;
        }
    }
}

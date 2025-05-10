package maite.maite.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.BaseEntity;
import maite.maite.domain.Enum.MateStatus;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Mate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_id")
    private User mateUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MateStatus status;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = MateStatus.PENDING;
        }
    }

}

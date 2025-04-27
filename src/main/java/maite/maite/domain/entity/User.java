package maite.maite.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.Enum.Gender;
import maite.maite.domain.Enum.LoginProvider;
import maite.maite.domain.mapping.ChatRoomUser;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이메일은 고유값
    @Column(nullable = false, unique = true)
    private String email;

    // LOCAL 로그인일 때만 사용
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginProvider provider;

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String phonenumber;

    @Column(name = "refresh token")
    private String refreshToken;

    @OneToMany(mappedBy = "user")
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
}
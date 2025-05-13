package maite.maite.web.dto.User.Signup;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String email;
    private String password;
    private String name;
    private String phonenumber;
    private String address;
    private String profileImageUrl;
}

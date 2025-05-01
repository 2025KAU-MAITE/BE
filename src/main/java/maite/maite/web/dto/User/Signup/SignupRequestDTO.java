package maite.maite.web.dto.User.Signup;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Builder
@Service
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String email;
    private String password;
    private String name;
    private String phonenumber;
    private String address;
}

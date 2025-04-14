package maite.maite.web.controller;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.Gender;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.AuthService;
import maite.maite.web.dto.LoginRequest;
import maite.maite.web.dto.RefreshTokenRequest;
import maite.maite.web.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam Gender gender,
            @RequestParam String phonenumber
            ) {

        SignupRequest request = SignupRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .gender(gender)
                .phonenumber(phonenumber)
                .build();

        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = authService.reissueAccessToken(request.getRefreshToken());
        return ResponseEntity.ok().body(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        authService.logout(userDetails.getUser());
        return ResponseEntity.ok("로그아웃되었습니다.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok("내 이메일은 " + user.getEmail() + "이고, 이름은 " + user.getName() + "입니다.");
    }
}

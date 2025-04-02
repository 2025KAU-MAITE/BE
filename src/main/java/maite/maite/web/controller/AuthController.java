package maite.maite.web.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import maite.maite.config.JwtTokenProvider;
import maite.maite.domain.entity.User;
import maite.maite.repository.RefreshTokenRepository;
import maite.maite.repository.UserRepository;
import maite.maite.service.AuthService;
import maite.maite.service.RefreshTokenService;
import maite.maite.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

    //이메일/비밀번호 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    // 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        // 리프레시 토큰 검증
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    // 리프레시 토큰 유효성 검증
                    refreshTokenService.verifyExpiration(refreshToken);

                    // 사용자 정보 조회
                    User user = refreshToken.getUser();

                    // 새로운 액세스 토큰 발급
                    String newAccessToken = jwtTokenProvider.generateAccessToken(user);

                    return ResponseEntity.ok(new TokenResponse(newAccessToken, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 데이터베이스에 존재하지 않습니다."));
    }

    // 로그아웃
    @Transactional
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // 리프레시 토큰 삭제
        refreshTokenRepository.deleteByUserEmail(email);

        return ResponseEntity.ok().build();
    }

    // 현재 인증된 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        UserInfoResponse userInfo = new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        User user = authService.signup(signupRequestDTO);

        // 회원가입 성공 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "회원가입이 성공적으로 완료되었습니다.");
        response.put("userId", user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

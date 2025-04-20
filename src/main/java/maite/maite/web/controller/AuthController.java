package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.Enum.Gender;
import maite.maite.domain.entity.User;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.AuthService;
import maite.maite.web.dto.User.*;
import maite.maite.web.dto.User.Login.LoginRequest;
import maite.maite.web.dto.User.Login.LoginResponse;
import maite.maite.web.dto.User.Login.LoginResult;
import maite.maite.web.dto.User.Logout.LogoutResponse;
import maite.maite.web.dto.User.Refresh.RefreshRequest;
import maite.maite.web.dto.User.Refresh.RefreshResponse;
import maite.maite.web.dto.User.Signup.SignupRequestDTO;
import maite.maite.web.dto.User.Signup.SignupResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원가입")
    public ApiResponse<SignupResponseDTO.SignupResponse> signup(
            @Parameter(description = "이메일") @RequestParam String email,
            @Parameter(description = "비밀번호") @RequestParam String password,
            @Parameter(description = "이름") @RequestParam String name,
            @Parameter(description = "전화번호") @RequestParam String phonenumber,
            @Parameter(description = "주소") @RequestParam String address
    ) {
        SignupRequestDTO.SignupRequest request = new SignupRequestDTO.SignupRequest(email, password, name, phonenumber, address);
        User user = authService.signup(request);
        SignupResponseDTO.SignupResponse response = SignupResponseDTO.SignupResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isRegistered(true)
                .registeredAt(LocalDateTime.now())
                .message("회원가입이 성공적으로 완료되었습니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/signup/check")
    @Operation(summary = "이메일 중복확인", description = "회원가입 시 이메일 중복확인")
    public ApiResponse<SignupResponseDTO.SingupCheckResponse> emailCheck(
            @Parameter(description = "이메일") @RequestParam String email
    ){
        boolean isDuplicated = authService.isDuplicated(email);
        SignupResponseDTO.SingupCheckResponse response = SignupResponseDTO.SingupCheckResponse.builder()
                .isDuplicated(isDuplicated)
                .message(isDuplicated ? "이미 존재하는 이메일입니다." : email+"은 사용 가능합니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인")
    public ApiResponse<LoginResponse> login(
            @Parameter(description = "이메일") @RequestParam String email,
            @Parameter(description = "비밀번호") @RequestParam String password) {
        LoginRequest request = new LoginRequest(email, password);
        LoginResult result = authService.login(request);
        LoginResponse response = LoginResponse.builder()
                .accessToken(result.getAccessToken())
                .message("로그인되었습니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }


    @PostMapping("refresh")
    @Operation(summary = "토큰 재발급 API", description = "액세스 토큰 재발급")
    public ApiResponse<RefreshResponse> refresh(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        String refreshToken = userDetails.getUser().getRefreshToken();
        RefreshRequest request = new RefreshRequest(refreshToken);
        String newAccessToken = authService.reissueAccessToken(request.getRefreshToken());
        RefreshResponse response = new RefreshResponse(newAccessToken);
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃")
    public ApiResponse<LogoutResponse> logout(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        authService.logout(userDetails.getUser());
        LogoutResponse response = new LogoutResponse("성공적으로 로그아웃되었습니다.");
        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/me")
    @Operation(summary = "정보 조회 API", description = "정보 조회")
    public ApiResponse<InfoResponse> getMyInfo(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        InfoResponse response = InfoResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phonenumber(user.getPhonenumber())
                .build();
        return ApiResponse.onSuccess(response);
    }
}

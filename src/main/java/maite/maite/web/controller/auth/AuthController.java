package maite.maite.web.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.apiPayload.exception.handler.CommonExceptionHandler;
import maite.maite.domain.Enum.LoginProvider;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.security.CustomerUserDetails;
import maite.maite.security.JwtTokenProvider;
import maite.maite.service.S3Service;
import maite.maite.service.auth.AuthService;
import maite.maite.service.auth.SmsService;
import maite.maite.service.auth.VerificationService;
import maite.maite.web.dto.User.*;
import maite.maite.web.dto.User.Find.FindResponseDTO;
import maite.maite.web.dto.User.Find.ResetPasswordDTO;
import maite.maite.web.dto.User.Login.GoogleLoginRequest;
import maite.maite.web.dto.User.Login.LoginRequest;
import maite.maite.web.dto.User.Login.LoginResponse;
import maite.maite.web.dto.User.Login.LoginResult;
import maite.maite.web.dto.User.Logout.LogoutResponse;
import maite.maite.web.dto.User.Refresh.RefreshRequest;
import maite.maite.web.dto.User.Refresh.RefreshResponse;
import maite.maite.web.dto.User.Signup.SignupRequestDTO;
import maite.maite.web.dto.User.Signup.SignupResponseDTO;
import maite.maite.web.dto.User.Signup.SignupVerifyResponseDTO;
import maite.maite.web.dto.User.Signup.SocialSignupResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static maite.maite.apiPayload.code.status.ErrorStatus.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "AUTH", description = "회원 관련 API")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final VerificationService verificationService;
    private final S3Service s3Service;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 API", description = "회원가입")
    public ApiResponse<SignupResponseDTO.SignupResponse> signup(
            @RequestBody SignupRequestDTO signupRequestDTO
    ) {
        User user = authService.signup(signupRequestDTO);
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

    @PostMapping(value = "/signup/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입용 프로필 이미지 업로드", description = "S3에 프로필 이미지를 업로드하고 URL을 반환합니다.")
    public ApiResponse<String> uploadProfileImage(@RequestParam MultipartFile file) {
        String imageUrl = s3Service.uploadProfileImage(file);
        return ApiResponse.onSuccess(imageUrl);
    }

    @PostMapping("/signup/send-code")
    @Operation(summary = "인증번호 발송(회원가입) API", description = "회원가입 인증번호 발송")
    public ApiResponse<SignupVerifyResponseDTO.sendCodeForFindIdDto> sendVerificationCodeForSignup(
            @Parameter(description = "인증번호 발송 요청") @RequestBody FindResponseDTO.sendCodeRequestForSignup request
    ) {
        String phonenumber = request.getPhonenumber();
        String verificationCode = smsService.generateVerificationCode();
        boolean isSent = smsService.sendVerificationSms(phonenumber, verificationCode);
        if (isSent) {
            verificationService.saveVerification(phonenumber, verificationCode, 5);
            SignupVerifyResponseDTO.sendCodeForFindIdDto response = new SignupVerifyResponseDTO.sendCodeForFindIdDto("인증번호가 발송됭었습니다");
            return ApiResponse.onSuccess(response);
        } else {
            SignupVerifyResponseDTO.sendCodeForFindIdDto response = new SignupVerifyResponseDTO.sendCodeForFindIdDto("인증번호가 발송에 문제가 생겼습니다. 잠시후 다시 시도해주십시오.");
            return ApiResponse.onSuccess(response);
        }
    }

    @PostMapping("/signup/verify-code")
    @Operation(summary = "인증번호 검증(회원가입) API", description = "회원가입 인증번호 검증")
    public ApiResponse<SignupVerifyResponseDTO.signupCodeResponseDto> verifyCodeForSignup(
            @Parameter(description = "인증번호 검증 요청") @RequestBody FindResponseDTO.verifyCodeRequest request
    ) {
        String phonenumber = request.getPhonenumber();
        String verificationCode = request.getVerificationCode();

        boolean isValid = verificationService.verifyCode(phonenumber, verificationCode);

        if (isValid) {
            SignupVerifyResponseDTO.signupCodeResponseDto response = SignupVerifyResponseDTO.signupCodeResponseDto.builder()
                    .status(true)
                    .message("인증되었습니다.")
                    .build();

            return ApiResponse.onSuccess(response);
        } else {
            SignupVerifyResponseDTO.signupCodeResponseDto response = SignupVerifyResponseDTO.signupCodeResponseDto.builder()
                    .status(true)
                    .message("인증번호가 올바르지 않거나 만료되었습니다.")
                    .build();
            return ApiResponse.onSuccess(response);
        }
    }

    @GetMapping("/signup/check")
    @Operation(summary = "이메일 중복확인", description = "회원가입 시 이메일 중복확인")
    public ApiResponse<SignupResponseDTO.SignupCheckResponse> emailCheck(
            @Parameter(description = "이메일") @RequestParam String email
    ){
        boolean isDuplicated = authService.isDuplicated(email);
        SignupResponseDTO.SignupCheckResponse response = SignupResponseDTO.SignupCheckResponse.builder()
                .isDuplicated(isDuplicated)
                .message(isDuplicated ? "이미 존재하는 이메일입니다." : email+"은 사용 가능한 이메일니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인")
    public ApiResponse<LoginResponse> login(
            @Parameter(description = "로그인 정보 입력") @RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        LoginRequest request = new LoginRequest(email, password);
        LoginResult result = authService.login(request);
        LoginResponse response = LoginResponse.builder()
                .accessToken(result.getAccessToken())
                .message("로그인되었습니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/login-google")
    @Operation(summary = "구글 로그인 API", description = "로그인")
    public ApiResponse<LoginResponse> googleLogin(
            @Parameter(description = "구글 로그인") @RequestBody GoogleLoginRequest googleLoginRequest
    ) {
        LoginResult result = authService.googleLogin(googleLoginRequest);
        LoginResponse response = LoginResponse.builder()
                .accessToken(result.getAccessToken())
                .message("로그인되었습니다.")
                .build();
        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/find-id/send-code")
    @Operation(summary = "인증번호 발송(아이디 찾기) API", description = "아이디 찾기 인증번호 발송")
    public ApiResponse<FindResponseDTO.sendCodeForFindIdDto> sendVerificationCodeForFindId(
            @Parameter(description = "인증번호 발송 요청") @RequestBody FindResponseDTO.sendCodeRequestForEmail request
    ) {
        String phonenumber = request.getPhonenumber();
        String name = request.getName();

        userRepository.findByNameAndPhonenumber(name, phonenumber)
                .orElseThrow(() -> new CommonExceptionHandler(USER_NOT_FOUND_FOR_FIND_EMAIL));

        String verifcationCode = smsService.generateVerificationCode();
        boolean isSent = smsService.sendVerificationSms(phonenumber, verifcationCode);

        if (isSent) {
            verificationService.saveVerification(phonenumber, verifcationCode, 5);
            FindResponseDTO.sendCodeForFindIdDto response = new FindResponseDTO.sendCodeForFindIdDto("인증번호가 발송됭었습니다");
            return ApiResponse.onSuccess(response);
        } else {
            FindResponseDTO.sendCodeForFindIdDto response = new FindResponseDTO.sendCodeForFindIdDto("인증번호 발송에 문제가 생겼습니다. 잠시후 다시 시도해주십시오.");
            return ApiResponse.onSuccess(response);
        }
    }

    @PostMapping("/find-id/verify")
    @Operation(summary = "인증번호 검증(아이디 찾기) API", description = "인증번호 검증 후 아이디 반환")
    public ApiResponse<FindResponseDTO.findIdResponseDto> verifyAndFindEmail(
            @Parameter(description = "인증번호 검증 요청") @RequestBody FindResponseDTO.verifyCodeRequestForEmail request
    ) {
        String phonenumber = request.getPhonenumber();
        String verificationCode = request.getVerificationCode();
        String name = request.getName();

        boolean isValid = verificationService.verifyCode(phonenumber, verificationCode);

        if (isValid) {
            FindResponseDTO.findIdResponseDto response = FindResponseDTO.findIdResponseDto.builder()
                    .status(true)
                    .email(authService.findEmailByPhonenumber(name, phonenumber))
                    .build();
            return ApiResponse.onSuccess(response);
        } else {
            FindResponseDTO.findIdResponseDto response = FindResponseDTO.findIdResponseDto.builder()
                    .status(false)
                    .message("인증번호가 올바르지 않거나 만료되었습니다.")
                    .build();

            return ApiResponse.onSuccess(response);
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정 API", description = "비밀번호 찾기")
    public ApiResponse<String> resetPassword(
            @Parameter(description = "비밀번호 재설정") @RequestBody ResetPasswordDTO.resetPasswordRequest request
    ) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 필요"));
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return ApiResponse.onSuccess("비밀번호 변경 성공");
    }

    @PostMapping("/reset-password/send-code")
    @Operation(summary = "인증번호 발송(비밀번호 재설정) API", description = "비밀번호 재설정 인증번호 발송")
    public ApiResponse<ResetPasswordDTO.sendCodeForResetPasswordDto> sendVerificationCodeForResetPassword(
            @Parameter(description = "비밀번호 재설정") @RequestBody FindResponseDTO.sendCodeRequestForPass request
    ) {
        String name = request.getName();
        String email = request.getEmail();
        String phonenumber = request.getPhonenumber();

        User user = userRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> new CommonExceptionHandler(USER_NOT_FOUND_FOR_RESET_PASSWORD));

        if (user.getEmail().equals(request.getEmail())) {
            String verificationCode = smsService.generateVerificationCode();
            boolean isSent = smsService.sendVerificationSms(phonenumber, verificationCode);

            if (isSent) {
                verificationService.saveVerification(phonenumber, verificationCode, 5);
                ResetPasswordDTO.sendCodeForResetPasswordDto response = new ResetPasswordDTO.sendCodeForResetPasswordDto("인증번호가 발송됭었습니다");
                return ApiResponse.onSuccess(response);
            } else {
                ResetPasswordDTO.sendCodeForResetPasswordDto response = new ResetPasswordDTO.sendCodeForResetPasswordDto("인증번호 발송에 문제가 생겼습니다. 잠시후 다시 시도해주십시오.");
                return ApiResponse.onSuccess(response);
            }
        } else {
            return ApiResponse.onFailure("NOT_MATCH", "유저 정보와 이메일이 일치하지 않습니다.", null);
        }
    }

    @PostMapping("/reset-password/verify")
    @Operation(summary = "인증번호 검증(비밀번호 재설정) API", description = "인증번호 검증")
    public ApiResponse<ResetPasswordDTO.resetCodeResponseDto> verifyAndResetPassword(
            @Parameter(description = "전화번호") @RequestBody FindResponseDTO.verifyCodeRequest request
    ) {
        String phonenumber = request.getPhonenumber();
        String verificationCode = request.getVerificationCode();

        boolean isValid = verificationService.verifyCode(phonenumber, verificationCode);

        if (isValid) {
            ResetPasswordDTO.resetCodeResponseDto response = ResetPasswordDTO.resetCodeResponseDto.builder()
                    .status(true)
                    .message("인증되었습니다.")
                    .build();
            return ApiResponse.onSuccess(response);
        } else {
            ResetPasswordDTO.resetCodeResponseDto response = ResetPasswordDTO.resetCodeResponseDto.builder()
                    .status(false)
                    .message("인증번호가 올바르지 않거나 만료되었습니다.")
                    .build();
            return ApiResponse.onSuccess(response);
        }
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

    @PostMapping("/complete-social-signup")
    public ApiResponse<SocialSignupResponseDTO.socialSignupResponse> completeSocialSignup(
            @Parameter(description = "이메일") @RequestParam String email,
            @Parameter(description = "이름") @RequestParam String name,
            @Parameter(description = "제공자(GOOGLE)") @RequestParam String provider,
            @Parameter(description = "전화번호") @RequestParam String phonenumber,
            @Parameter(description = "주소") @RequestParam String address) {

        // 이메일 중복 확인
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CommonExceptionHandler(EMAIL_ALREADY_EXIST);
        }
        LoginProvider loginProvider = LoginProvider.GOOGLE;

        // 사용자 생성
        User user = User.builder()
                .email(email)
                .name(name)
                .provider(loginProvider)
                .phonenumber(phonenumber)
                .address(address)
                .build();

        userRepository.save(user);

        SocialSignupResponseDTO.socialSignupResponse response = SocialSignupResponseDTO.socialSignupResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phonenumber(user.getPhonenumber())
                .address(user.getAddress())
                .provider(user.getProvider().name())
                .build();
        return ApiResponse.onSuccess(response);
    }
}

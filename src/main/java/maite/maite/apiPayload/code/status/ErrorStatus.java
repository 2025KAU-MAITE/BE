package maite.maite.apiPayload.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import maite.maite.apiPayload.code.BaseErrorCode;
import maite.maite.apiPayload.code.ErrorReasonDTO;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    NOT_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4006", "비밀번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4005", "이미 존재하는 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    SMS_SEND_FAIL(HttpStatus.BAD_GATEWAY, "SMS4001", "인증번호를 발송할 수 없습니다"),
    USER_NOT_FOUND_FOR_FIND_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER4010", "일치하는 유저 정보가 존재하지 않습니다."),
    USER_NOT_FOUND_FOR_RESET_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER4011", "일치하는 유저 정보가 존재하지 않습니다"),

    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
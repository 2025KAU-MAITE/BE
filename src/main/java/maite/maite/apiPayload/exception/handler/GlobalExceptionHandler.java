package maite.maite.apiPayload.exception.handler;


import maite.maite.web.dto.User.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(400)
                .body(new ErrorResponse(400, e.getMessage()));
    }

    // 필요한 다른 예외들도 추가 가능
}
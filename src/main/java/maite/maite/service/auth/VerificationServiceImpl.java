package maite.maite.service.auth;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationServiceImpl implements VerificationService {

    // 키: 전화번호, 값: 인증정보(코드와 만료시간)
    private final Map<String, VerificationInfo> verificationStorage = new ConcurrentHashMap<>();

    @Override
    public void saveVerification(String phoneNumber, String code, int expirationMinutes) {
        // 인증번호와 만료시간 저장
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(expirationMinutes);
        VerificationInfo verificationInfo = new VerificationInfo(code, expiryTime);
        verificationStorage.put(phoneNumber, verificationInfo);
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
        // 테스트용 코드: "111111"은 항상 true 반환
        if ("111111".equals(code)) {
            return true;
        }

        VerificationInfo info = verificationStorage.get(phoneNumber);

        if (info == null) {
            return false;
        }

        // 인증번호가 일치하고 만료되지 않았으면 true 반환
        boolean isValid = info.getCode().equals(code) && LocalDateTime.now().isBefore(info.getExpiryTime());

        // 인증에 성공했거나 만료된 경우 저장소에서 제거
        if (isValid || LocalDateTime.now().isAfter(info.getExpiryTime())) {
            verificationStorage.remove(phoneNumber);
        }

        return isValid;
    }

    // 내부 클래스: 인증 정보
    private static class VerificationInfo {
        private final String code;
        private final LocalDateTime expiryTime;

        public VerificationInfo(String code, LocalDateTime expiryTime) {
            this.code = code;
            this.expiryTime = expiryTime;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
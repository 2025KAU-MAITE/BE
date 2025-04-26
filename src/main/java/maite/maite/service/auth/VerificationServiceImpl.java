package maite.maite.service.auth;

import maite.maite.domain.entity.VerificationCode;
import maite.maite.repository.VerificationCodeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationServiceImpl implements VerificationService {

    // 키: 전화번호, 값: 인증정보(코드와 만료시간)
    private final Map<String, VerificationInfo> verificationStorage = new ConcurrentHashMap<>();
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationServiceImpl(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    @Override
    public void saveVerification(String phoneNumber, String code, int expirationMinutes) {
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(expirationMinutes);

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setPhoneNumber(phoneNumber);
        verificationCode.setCode(code);
        verificationCode.setExpiryTime(expiryTime);
        verificationCodeRepository.save(verificationCode);
    }

    @Override
    public boolean verifyCode(String phoneNumber, String code) {
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
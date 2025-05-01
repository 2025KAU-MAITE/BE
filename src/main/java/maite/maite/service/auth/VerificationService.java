package maite.maite.service.auth;

public interface VerificationService {

    /**
     * 인증번호를 저장합니다.
     *
     * @param phoneNumber 전화번호
     * @param code 인증번호
     * @param expirationMinutes 유효시간(분)
     */
    void saveVerification(String phoneNumber, String code, int expirationMinutes);

    /**
     * 인증번호의 유효성을 검증합니다.
     *
     * @param phoneNumber 전화번호
     * @param code 인증번호
     * @return 유효한 인증번호이면 true, 아니면 false
     */
    boolean verifyCode(String phoneNumber, String code);
}
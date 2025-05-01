package maite.maite.service.auth;

public interface SmsService {

    /**
     * 인증번호를 생성합니다.
     *
     * @return 생성된 인증번호
     */
    String generateVerificationCode();

    /**
     * 인증번호를 SMS로 발송합니다.
     *
     * @param phoneNumber 수신자 전화번호
     * @param verificationCode 인증번호
     * @return 발송 성공 여부
     */
    boolean sendVerificationSms(String phoneNumber, String verificationCode);
}
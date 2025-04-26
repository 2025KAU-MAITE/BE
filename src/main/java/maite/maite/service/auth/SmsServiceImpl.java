package maite.maite.service.auth;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.from.number}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        // 메시지 서비스 초기화
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public String generateVerificationCode() {
        // 6자리 난수 생성
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public boolean sendVerificationSms(String phoneNumber, String verificationCode) {
        try {
            System.out.println("Send Start");
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(phoneNumber);
            message.setText("[MAITE] 인증번호 " + verificationCode + "를 입력해주세요.");

            // 메시지 발송
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

            // 메시지 발송 성공 여부 확인
            logger.info("SMS 발송 완료: {}", response);
            return response != null && "2000".equals(response.getStatusCode());
        } catch (Exception e) {
            logger.error("SMS 발송 실패: {}", e.getMessage(), e);
            return false;
        }
    }
}
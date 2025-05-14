package maite.maite.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenUtil {

    private final String clientId;

    public GoogleTokenUtil(@Value("${oauth.google.client-id}") String clientId) {
        this.clientId = clientId;
    }

    public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(clientId)) // 설정된 clientId 사용
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new IllegalArgumentException("Invalid ID token");
            }
        } catch (Exception e) {
            throw new RuntimeException("ID 토큰 검증 실패", e);
        }
    }
}

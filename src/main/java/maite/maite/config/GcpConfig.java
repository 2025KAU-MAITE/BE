package maite.maite.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class GcpConfig {

    @Value("${spring.cloud.gcp.credentials.encoded-key}")
    private String gcpCredentialsBase64;

    @Bean
    public CredentialsProvider credentialsProvider() throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(gcpCredentialsBase64);
        return FixedCredentialsProvider.create(
                ServiceAccountCredentials.fromStream(new ByteArrayInputStream(decodedBytes))
        );
    }
}
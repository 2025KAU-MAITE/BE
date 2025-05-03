package maite.maite.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GcpConfig {

    @Value("${spring.cloud.gcp.credentials.encoded-key}")
    private String gcpCredentials;

    @Bean
    public CredentialsProvider credentialsProvider() throws IOException {
        return FixedCredentialsProvider.create(
                ServiceAccountCredentials.fromStream(
                        new ByteArrayInputStream(gcpCredentials.getBytes(StandardCharsets.UTF_8))
                )
        );
    }
}
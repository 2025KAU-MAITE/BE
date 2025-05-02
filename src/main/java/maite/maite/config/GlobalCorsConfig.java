package maite.maite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 허용
                .allowedOriginPatterns("*")  // Spring Boot 2.4+에서는 allowedOrigins 대신 이거 사용
                .allowedMethods("*")         // GET, POST, PUT 등 모든 메서드 허용
                .allowedHeaders("*")         // 모든 헤더 허용
                .allowCredentials(true);     // 쿠키/인증 정보 전송 허용
    }
}
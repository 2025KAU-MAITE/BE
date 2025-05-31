package maite.maite.config;

import lombok.RequiredArgsConstructor;
import maite.maite.security.CustomerUserDetailsService;
import maite.maite.security.JwtAuthenticationFilter;
import maite.maite.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider, CustomerUserDetailsService customerUserDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/health",
                                "/actuator/**",
                                "/auth/**",
                                "/api/**",
                                "/rooms/**",
                                "/swagger-ui/**",
                                //"/Chat.html",         // 채팅 테스트용 html
                                //"test-toss.html",     // 토스 결제 테스트용 html
                                //"/toss/**",           // 토스 결제 테스트 관련 API
                                "/ws-chat/**",
                                "/kakao/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customerUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

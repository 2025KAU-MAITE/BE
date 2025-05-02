package maite.maite.config;

import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.exception.handler.OAuth2AuthenticationFailureHandler;
import maite.maite.apiPayload.exception.handler.OAuth2AuthenticationSuccessHandler;
import maite.maite.security.CustomOAuth2UserService;
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

    private final JwtTokenProvider jwtTokenProvider;
     private final CustomOAuth2UserService customOAuth2UserService;
     private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
     private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider, CustomerUserDetailsService customerUserDetailsService) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/api/**",
                                "/rooms/**",
                                "/swagger-ui/**",
                                //"/Chat.html",
                                "/ws-chat/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customerUserDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

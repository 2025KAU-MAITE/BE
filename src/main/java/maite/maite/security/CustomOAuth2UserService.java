package maite.maite.security;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.LoginProvider;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 소셜 로그인 제공자 정보 (google)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 구글에서 제공하는 사용자 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 이메일로 사용자 찾기
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            // 이미 가입된 사용자면 기존 정보 반환
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("user", userOptional.get());
            attributes.put("needAdditionalInfo", false);

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email");  // 일관된 nameAttributeKey 사용
        } else {
            // 신규 사용자는 추가 정보가 필요하므로 임시 상태로 표시
            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("needAdditionalInfo", true);
            attributes.put("tempEmail", email);
            attributes.put("tempName", name);
            attributes.put("provider", LoginProvider.GOOGLE.name());

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email");  // 일관된 nameAttributeKey 사용
        }
    }
}

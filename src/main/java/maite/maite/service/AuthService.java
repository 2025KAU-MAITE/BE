package maite.maite.service;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.web.dto.SignupRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(SignupRequestDTO signupRequestDTO) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(signupRequestDTO.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 새 사용자 생성 및 저장
        User user = new User();
        user.setEmail(signupRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        user.setName(signupRequestDTO.getName());

        return userRepository.save(user);
    }
}
package maite.maite.service.mate;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.Mate;
import maite.maite.domain.entity.User;
import maite.maite.repository.MateRepository;
import maite.maite.repository.UserRepository;
import maite.maite.web.dto.mate.response.MateResponseDto;
import maite.maite.web.dto.mate.response.UserSearchResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MateServiceImpl implements MateService {

    private final MateRepository mateRepository;
    private final UserRepository userRepository;

    @Override
    public void addMate(User user, Long userId) {
        User mateUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Check if user is trying to add themselves
        if (user.getId().equals(mateUser.getId())) {
            throw new IllegalArgumentException("자기 자신을 친구로 추가할 수 없습니다.");
        }

        // Check if already mates
        if (mateRepository.existsByUserAndMateUser(user, mateUser)) {
            throw new IllegalArgumentException("이미 친구입니다.");
        }

        // Create mate relationship (both ways for bidirectional friendship)
        Mate mate1 = Mate.builder()
                .user(user)
                .mateUser(mateUser)
                .build();

        Mate mate2 = Mate.builder()
                .user(mateUser)
                .mateUser(user)
                .build();

        mateRepository.save(mate1);
        mateRepository.save(mate2);
    }

    @Override
    public void removeMate(User user, Long mateId) {
        User mateUser = userRepository.findById(mateId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Find and delete the relationship (both directions)
        Mate mate1 = mateRepository.findByUserAndMateUser(user, mateUser)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        Mate mate2 = mateRepository.findByUserAndMateUser(mateUser, user)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        mateRepository.delete(mate1);
        mateRepository.delete(mate2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MateResponseDto> getMates(User user) {
        List<Mate> mates = mateRepository.findAllByUser(user);

        return mates.stream()
                .map(mate -> MateResponseDto.builder()
                        .id(mate.getMateUser().getId())
                        .mateId(mate.getId())
                        .name(mate.getMateUser().getName())
                        .email(mate.getMateUser().getEmail())
                        .profileImageUrl(mate.getMateUser().getProfileImageUrl())
                        .createdAt(mate.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUsers(User user, String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("검색어를 입력해주세요.");
        }

        List<User> searchResults = mateRepository.searchUsers(query, user.getId());

        return searchResults.stream()
                .map(result -> {
                    // Check if already mates
                    boolean isMate = mateRepository.existsByUserAndMateUser(user, result);

                    return UserSearchResponseDto.builder()
                            .id(result.getId())
                            .name(result.getName())
                            .email(result.getEmail())
                            .profileImageUrl(result.getProfileImageUrl())
                            .isMate(isMate)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

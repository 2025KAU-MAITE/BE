package maite.maite.service.mate;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.Enum.MateStatus;
import maite.maite.domain.entity.Mate;
import maite.maite.domain.entity.User;
import maite.maite.repository.MateRepository;
import maite.maite.repository.UserRepository;
import maite.maite.web.dto.mate.response.MateResponseDto;
import maite.maite.web.dto.mate.response.PendingMateResponseDto;
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
    @Transactional(readOnly = true)
    public List<MateResponseDto> getMates(User user) {
        // 수락된 친구만 조회
        List<Mate> mates = mateRepository.findAllByUserAndStatus(user, MateStatus.ACCEPTED);

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
    public void requestMate(User user, Long userId){
        User mateUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 자기 자신에게 요청하는 경우 예외 처리
        if (user.getId().equals(mateUser.getId())) {
            throw new IllegalArgumentException("자기 자신을 친구로 추가할 수 없습니다.");
        }

        // 이미 친구이거나 요청한 경우 예외 처리
        if (mateRepository.existsByUserAndMateUser(user, mateUser)) {
            throw new IllegalArgumentException("이미 친구이거나 요청을 보냈습니다.");
        }

        // 반대로 이미 친구 요청을 받은 경우
        if (mateRepository.existsByUserAndMateUser(mateUser, user)) {
            throw new IllegalArgumentException("이미 상대방으로부터 친구 요청을 받았습니다. 친구 요청 목록을 확인해주세요.");
        }

        Mate mate = Mate.builder()
                .user(user)
                .mateUser(mateUser)
                .status(MateStatus.PENDING)
                .build();

        mateRepository.save(mate);
    }

    @Override
    public void acceptMateRequest(User user, Long requestId) {
        Mate request = mateRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 요청을 받은 사용자가 맞는지 확인
        if (!request.getMateUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 요청 상태 확인
        if (request.getStatus() != MateStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        // 요청 수락
        request.setStatus(MateStatus.ACCEPTED);
        mateRepository.save(request);

        // 양방향 친구 관계 생성 (상대방 -> 사용자 방향도 친구로 설정)
        Mate reverseMate = Mate.builder()
                .user(request.getMateUser())
                .mateUser(request.getUser())
                .status(MateStatus.ACCEPTED)
                .build();

        mateRepository.save(reverseMate);
    }

    @Override
    public void rejectMateRequest(User user, Long requestId) {
        Mate request = mateRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("친구 요청을 찾을 수 없습니다."));

        // 요청을 받은 사용자가 맞는지 확인
        if (!request.getMateUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 요청 상태 확인
        if (request.getStatus() != MateStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        // 요청 거절
        request.setStatus(MateStatus.REJECTED);
        mateRepository.save(request);
    }

    @Override
    public void removeMate(User user, Long mateId) {
        User mateUser = userRepository.findById(mateId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 친구 관계 조회
        Mate mate1 = mateRepository.findByUserAndMateUserAndStatus(user, mateUser, MateStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        Mate mate2 = mateRepository.findByUserAndMateUserAndStatus(mateUser, user, MateStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계를 찾을 수 없습니다."));

        // 친구 관계 삭제
        mateRepository.delete(mate1);
        mateRepository.delete(mate2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingMateResponseDto> getPendingMateRequests(User user) {
        // 대기 중인 요청만 조회
        List<Mate> pendingRequests = mateRepository.findAllByMateUserAndStatus(user, MateStatus.PENDING);

        return pendingRequests.stream()
                .map(mate -> PendingMateResponseDto.builder()
                        .requestId(mate.getId())
                        .userId(mate.getUser().getId())
                        .name(mate.getUser().getName())
                        .email(mate.getUser().getEmail())
                        .profileImageUrl(mate.getUser().getProfileImageUrl())
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
                    // 친구 관계 확인
                    boolean isMate = mateRepository.existsByUserAndMateUserAndStatus(user, result, MateStatus.ACCEPTED);
                    // 친구 요청 보냄 확인
                    boolean isPendingSent = mateRepository.existsByUserAndMateUserAndStatus(user, result, MateStatus.PENDING);
                    // 친구 요청 받음 확인
                    boolean isPendingReceived = mateRepository.existsByUserAndMateUserAndStatus(result, user, MateStatus.PENDING);

                    return UserSearchResponseDto.builder()
                            .id(result.getId())
                            .name(result.getName())
                            .email(result.getEmail())
                            .profileImageUrl(result.getProfileImageUrl())
                            .isMate(isMate)
                            .isPendingSent(isPendingSent)
                            .isPendingReceived(isPendingReceived)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

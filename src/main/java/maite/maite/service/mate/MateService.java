package maite.maite.service.mate;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.mate.response.MateResponseDto;
import maite.maite.web.dto.mate.response.PendingMateResponseDto;
import maite.maite.web.dto.mate.response.UserSearchResponseDto;

import java.util.List;

public interface MateService {
    void requestMate(User user, Long userId);
    void acceptMateRequest(User user, Long requestId);
    void rejectMateRequest(User user, Long requestId);
    void removeMate(User user, Long mateId);
    List<MateResponseDto> getMates(User user);
    List<PendingMateResponseDto> getPendingMateRequests(User user);
    List<UserSearchResponseDto> searchUsers(User user, String query);
}

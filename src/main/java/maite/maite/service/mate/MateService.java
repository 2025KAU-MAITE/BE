package maite.maite.service.mate;

import maite.maite.domain.entity.User;
import maite.maite.web.dto.mate.response.MateResponseDto;
import maite.maite.web.dto.mate.response.UserSearchResponseDto;

import java.util.List;

public interface MateService {
    void addMate(User user, Long UserId);
    void removeMate(User user, Long mateId);
    List<MateResponseDto> getMates(User user);
    List<UserSearchResponseDto> searchUsers(User user, String query);
}

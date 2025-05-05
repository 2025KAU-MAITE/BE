package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.mate.MateService;
import maite.maite.web.dto.mate.request.MateRequestDto;
import maite.maite.web.dto.mate.response.MateResponseDto;
import maite.maite.web.dto.mate.response.UserSearchResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mates")
@RequiredArgsConstructor
@Tag(name = "Mate", description = "Mate 관련 API")
public class MateController {

    private final MateService mateService;

    @Operation(summary = "친구 목록 조회 API")
    @GetMapping
    public ApiResponse<List<MateResponseDto>> getMates(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        return ApiResponse.onSuccess(mateService.getMates(userDetails.getUser()));
    }

    @Operation(summary = "친구 추가 API")
    @PostMapping
    public ApiResponse<Void> addMate(
            @RequestBody MateRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        mateService.addMate(userDetails.getUser(), request.getUserId());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "친구 삭제 API")
    @DeleteMapping("/{mateId}")
    public ApiResponse<Void> deleteMate(
            @PathVariable Long mateId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        mateService.removeMate(userDetails.getUser(), mateId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "사용자 검색 API")
    @GetMapping("/search")
    public ApiResponse<List<UserSearchResponseDto>> searchUsers(
            @RequestParam String query,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(mateService.searchUsers(userDetails.getUser(), query));
    }
}

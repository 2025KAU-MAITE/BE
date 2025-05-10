package maite.maite.service.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import maite.maite.config.NaverConfig;
import maite.maite.web.dto.map.response.CafeResponse;
import maite.maite.web.dto.map.response.GeocodeResponse;
import maite.maite.web.dto.map.response.ReverseGeocodeResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static maite.maite.config.NaverConfig.REVERSE_GEOCODE_API_URL;
import static maite.maite.config.NaverConfig.SEARCH_API_URL;

@Service
@RequiredArgsConstructor
public class NaverMapService {

    private final NaverConfig naverConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public GeocodeResponse.Address getCoordinatesFromAddress(String address) {
        String url = NaverConfig.GEOCODE_API_URL + "?query=" + address;

        ResponseEntity<GeocodeResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, createAuthHeaders(), GeocodeResponse.class
        );

        GeocodeResponse body = response.getBody();

        if (body == null || body.getAddresses() == null || body.getAddresses().isEmpty()) {
            throw new IllegalStateException("주소를 변환할 수 없습니다: " + address);
        }

        return body.getAddresses().get(0);
    }

    public String getLocationNameFromCoordinates(double latitude, double longitude) {
        String url = String.format(
                REVERSE_GEOCODE_API_URL,
                longitude, latitude
        );

        ResponseEntity<ReverseGeocodeResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, createAuthHeaders(), ReverseGeocodeResponse.class
        );

        ReverseGeocodeResponse body = response.getBody();
        if (body == null || body.getResults() == null || body.getResults().isEmpty()) {
            throw new IllegalStateException("위치명 조회 실패: " + latitude + ", " + longitude);
        }

        ReverseGeocodeResponse.Result result = body.getResults().get(0);
        return result.getRegion().getArea2().getName() + " " + result.getRegion().getArea3().getName();
    }


    public List<CafeResponse> getNearbyCafes(String address) {
        String url = SEARCH_API_URL + address + " 카페" + "&display=5&sort=comment";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverConfig.getSeachClientId());
        headers.set("X-Naver-Client-Secret", naverConfig.getSearchClientSecret());

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        List<CafeResponse> cafes = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                cafes.add(CafeResponse.builder()
                        .title(item.path("title").asText().replaceAll("<[^>]*>", ""))  // <b>태그 제거
                        .category(item.path("category").asText())
                        .roadAddress(item.path("roadAddress").asText())
                        .description(item.path("description").asText())
                        .mapx(item.path("mapx").asText())
                        .mapy(item.path("mapy").asText())
                        .build());
            }

        } catch (Exception e) {
            throw new RuntimeException("네이버 지역 검색 응답 파싱 실패", e);
        }

        return cafes;
    }



    // 🔧 인증 헤더 공통 메서드
    private HttpEntity<Void> createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverConfig.getMapClientId());
        headers.set("X-NCP-APIGW-API-KEY", naverConfig.getMapClientSecret());
        return new HttpEntity<>(headers);
    }

    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

}
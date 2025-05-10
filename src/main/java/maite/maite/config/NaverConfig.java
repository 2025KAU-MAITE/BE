package maite.maite.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class NaverConfig {

    @Value("${naver.map.client-id}")
    private String mapClientId;

    @Value("${naver.map.client-secret}")
    private String mapClientSecret;

    @Value("${naver.search.client-id}")
    private String seachClientId;

    @Value("${naver.search.client-secret}")
    private String searchClientSecret;


    public static final String GEOCODE_API_URL = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode";
    public static final String REVERSE_GEOCODE_API_URL = "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=%f,%f&orders=roadaddr,admcode&output=json";

    public static final String SEARCH_API_URL = "https://openapi.naver.com/v1/search/local.json?query=";

}
package maite.maite.web.dto.map.response;

import lombok.Getter;
import java.util.List;

@Getter
public class ReverseGeocodeResponse {
    private List<Result> results;

    @Getter
    public static class Result {
        private Region region;

        @Getter
        public static class Region {
            private Area area1;
            private Area area2;
            private Area area3;

            @Getter
            public static class Area {
                private String name;
            }
        }
    }
}
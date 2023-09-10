package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

    private static final String API_PREFIX = "/hit";
    private static final String GET_STAT_PATH = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(EndpointHitDto hitDto) {
        return post(API_PREFIX, hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        String urisStr = String.join(",", uris);

        String startStr = DF.format(start);

        String endStr = DF.format(end);

        Map<String, Object> parameters = Map.of(
                "start", startStr,
                "end", endStr,
                "uris", urisStr,
                "unique", unique
        );
        return get(GET_STAT_PATH, parameters);
    }
}

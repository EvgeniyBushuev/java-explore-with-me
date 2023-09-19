package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    private static final String API_PREFIX = "/hit";
    private static final String GET_STAT_PATH = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

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

        String startStr = start.format(DF);

        String endStr = end.format(DF);

        Map<String, Object> parameters = Map.of(
                "start", startStr,
                "end", endStr,
                "uris", urisStr,
                "unique", unique
        );
        return get(GET_STAT_PATH, parameters);
    }
}
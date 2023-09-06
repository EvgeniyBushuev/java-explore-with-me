package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.ewm.stats.client.BaseClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

    private static final String API_PREFIX = "/hit";
    private static final String GET_STAT_PATH = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

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

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get(GET_STAT_PATH, parameters);
    }
}

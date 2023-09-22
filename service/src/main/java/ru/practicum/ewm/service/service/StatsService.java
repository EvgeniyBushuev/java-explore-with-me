package ru.practicum.ewm.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.storage.ParticipationRequestRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsClient statsClient;
    private final ParticipationRequestRepository requestRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value(value = "${app.name}")
    private String appName;

    public void addHit(HttpServletRequest request) {

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(appName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .hitTimestamp(LocalDateTime.now())
                .build();

        statsClient.addHit(hitDto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, unique);

        try {
            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
        } catch (IOException exception) {
            throw new ClassCastException(exception.getMessage());
        }
    }

    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();

        List<Event> publishedEvents = getPublished(events);

        if (events.isEmpty()) {
            return views;
        }

        Optional<LocalDateTime> minPublishedOn = publishedEvents.stream()
                .map(Event::getPublishedDate)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        if (minPublishedOn.isPresent()) {
            LocalDateTime start = minPublishedOn.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(event -> ("/events/" + event.getId()))
                    .collect(Collectors.toList());
            List<ViewStatsDto> stats = getStats(start, end, uris, true);
            stats.forEach(stat -> {
                Long eventId = Long.parseLong(stat.getUri()
                        .split("/", 0)[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
            });
        }

        return views;
    }

    public List<Event> getPublished(List<Event> events) {
        return events.stream()
                .filter(event -> event.getPublishedDate() != null)
                .collect(Collectors.toList());
    }
}
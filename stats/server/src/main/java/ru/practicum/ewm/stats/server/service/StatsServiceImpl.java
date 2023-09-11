package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.mapper.HitMapper;
import ru.practicum.ewm.stats.server.storage.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        statsRepository.save(HitMapper.fromDto(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        List<ViewStatsDto> statsDtoList;

        if (unique) {
            statsDtoList = statsRepository.findUniqueStats(start, end, uris);
        } else {
            statsDtoList = statsRepository.findNotUniqueStats(start, end, uris);
        }

        return statsDtoList.stream()
                .map(statsDto -> ViewStatsDto.builder()
                        .app(statsDto.getApp())
                        .uri(statsDto.getUri())
                        .hits(statsDto.getHits())
                        .build())
                .collect(Collectors.toList());
    }
}

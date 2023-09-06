package ru.practicum.ewm.stats.server.mapper;

import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;

public class HitMapper {
    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .hitTimestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit fromDto(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getHitTimestamp());
        return endpointHit;
    }
}

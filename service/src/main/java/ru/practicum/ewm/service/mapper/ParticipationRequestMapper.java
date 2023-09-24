package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.model.ParticipationRequest;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto toDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .created(participationRequest.getCreatedTime())
                .build();
    }
}
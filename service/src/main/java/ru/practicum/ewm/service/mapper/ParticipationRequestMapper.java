package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.model.ParticipationRequest;
import ru.practicum.ewm.service.model.enus.Status;
import ru.practicum.ewm.service.model.User;

import java.time.LocalDateTime;

public class ParticipationRequestMapper {
    public static ParticipationRequest fromDto(User user, Event event) {
        Status status;
        if (event.getParticipantLimit() == 0 || !event.getIsRequestModeration()) {
            status = Status.CONFIRMED;
        } else {
            status = Status.PENDING;
        }
        return ParticipationRequest.builder()
                .createdTime(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();
    }

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

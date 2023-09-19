package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.event.FullEventDto;
import ru.practicum.ewm.service.dto.event.NewEventDto;
import ru.practicum.ewm.service.dto.event.ShortEventDto;
import ru.practicum.ewm.service.model.*;
import ru.practicum.ewm.service.model.enus.State;

import java.time.LocalDateTime;

public class EventMapper {
    public static Event fromDto(NewEventDto newEventDto,
                                Category category,
                                User user,
                                Location location,
                                State state) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .eventDate(newEventDto.getEventDate())
                .description(newEventDto.getDescription())
                .publishedDate(LocalDateTime.now())
                .initiator(user)
                .location(location)
                .isPaid(newEventDto.getIsPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .isRequestModeration(newEventDto.getIsRequestModeration())
                .state(state)
                .title(newEventDto.getTitle())
                .build();
    }

    public static ShortEventDto toShortDto(Event event) {
        return ShortEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .isPaid(event.getIsPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static FullEventDto toFullDto(Event event) {
        return FullEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .eventDate(event.getEventDate())
                .createdOn(event.getCreateDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .isPaid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedDate())
                .isRequestModeration(event.getIsRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }
}
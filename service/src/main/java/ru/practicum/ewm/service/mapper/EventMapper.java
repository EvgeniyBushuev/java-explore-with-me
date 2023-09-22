package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.event.FullEventDto;
import ru.practicum.ewm.service.dto.event.NewEventDto;
import ru.practicum.ewm.service.dto.event.ShortEventDto;
import ru.practicum.ewm.service.model.*;
import ru.practicum.ewm.service.model.enums.State;

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
                .isPaid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .isRequestModeration(newEventDto.getRequestModeration())
                .state(state)
                .title(newEventDto.getTitle())
                .build();
    }

    public static ShortEventDto toShortDto(Event event) {
        return ShortEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .paid(event.getIsPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static FullEventDto toFullDto(Event event) {
        return FullEventDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .createdOn(event.getCreateDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(event.getLocation())
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedDate())
                .requestModeration(event.getIsRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }
}
package ru.practicum.ewm.service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.event.FullEventDto;
import ru.practicum.ewm.service.dto.event.NewEventDto;
import ru.practicum.ewm.service.dto.event.ShortEventDto;
import ru.practicum.ewm.service.dto.event.UserUpdateEventDto;
import ru.practicum.ewm.service.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventControllerPrivate {
    private final EventService eventService;

    @GetMapping
    public List<ShortEventDto> getAll(@PathVariable long userId,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        return eventService.getAllByPrivate(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public FullEventDto getById(@PathVariable long userId,
                                    @PathVariable long eventId) {
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllByPrivate(@PathVariable long userId,
                                                                             @PathVariable long eventId) {
        return eventService.getParticipationRequestsByPrivate(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto create(@PathVariable long userId,
                               @Valid @RequestBody NewEventDto newDto) {
        log.info("Создание нового события пользователем с id {} и параметрами {}", userId, newDto);
        return eventService.addByPrivate(userId, newDto);
    }

    @PatchMapping("/{eventId}")
    public FullEventDto patchEvent(@PathVariable long userId,
                                       @PathVariable long eventId,
                                       @Valid @RequestBody UserUpdateEventDto userUpdateEventDto) {
        log.info("Запрос на измение события пользователем с id {}", userId);
        return eventService.updateByPrivate(userId, eventId, userUpdateEventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchEventRequests(@PathVariable long userId,
                                                             @PathVariable long eventId,
                                                             @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Запрос на измение события организатором с id {}", userId);
        return eventService.patchParticipationRequestsByPrivate(userId, eventId, eventRequestStatusUpdateRequest);
    }
}

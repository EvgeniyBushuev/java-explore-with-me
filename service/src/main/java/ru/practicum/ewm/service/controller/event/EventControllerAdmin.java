package ru.practicum.ewm.service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.event.FullEventDto;
import ru.practicum.ewm.service.dto.event.UpdateEventDto;
import ru.practicum.ewm.service.model.enus.State;
import ru.practicum.ewm.service.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class EventControllerAdmin {

    private final EventService eventService;
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FullEventDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeEnd,
            @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
            @Valid @RequestParam(defaultValue = "10") @Min(10) int size) {

        log.info("Запрос списка событий от ADMIN");
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public FullEventDto patchEventByAdmin(@PathVariable Long eventId,
                                          @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("Запрос на обновление события c ID {} от ADMIN", eventId);
        return eventService.updateByAdmin(eventId, updateEventDto);
    }
}

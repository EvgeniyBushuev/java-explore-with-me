package ru.practicum.ewm.service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.event.FullEventDto;
import ru.practicum.ewm.service.dto.event.ShortEventDto;
import ru.practicum.ewm.service.model.enus.SortType;
import ru.practicum.ewm.service.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPublic {
    private final EventService eventService;

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<ShortEventDto> getAll(@RequestParam(defaultValue = "") String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeStart,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = FORMAT) LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @RequestParam(defaultValue = "VIEWS") SortType sort,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) int size,
                                      HttpServletRequest request) {

        log.info("Запрос списка событий от PUBLIC ");
        return eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("{eventId}")
    public FullEventDto getById(@PathVariable long eventId,
                                    HttpServletRequest request) {

        log.info("Запрос события с ID {} от PUBLIC", eventId);
        return eventService.getEventByPublic(eventId, request);
    }

}

package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.dto.event.*;
import ru.practicum.ewm.service.dto.location.LocationDto;
import ru.practicum.ewm.service.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.exception.BadRequestException;
import ru.practicum.ewm.service.exception.DataConflictException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.EventMapper;
import ru.practicum.ewm.service.mapper.LocationMapper;
import ru.practicum.ewm.service.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.service.model.*;
import ru.practicum.ewm.service.model.enus.SortType;
import ru.practicum.ewm.service.model.enus.State;
import ru.practicum.ewm.service.model.enus.Status;
import ru.practicum.ewm.service.storage.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.service.model.enus.Status.CONFIRMED;
import static ru.practicum.ewm.service.model.enus.Status.REJECTED;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsService statsService;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    public List<FullEventDto> getAllByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        List<FullEventDto> fullEventDtos = events.stream()
                .map(EventMapper::toFullDto).collect(Collectors.toList());

        for (FullEventDto fullDto : fullEventDtos) {
            fullDto.setConfirmedRequests(confirmedRequests.getOrDefault(fullDto.getId(), 0L));
            fullDto.setViews(views.getOrDefault(fullDto.getId(), 0L));
        }

        return fullEventDtos;
    }

    @Transactional
    public FullEventDto updateByAdmin(Long eventId, UpdateEventDto updateEventDto) {

        checkEventDate(updateEventDto.getEventDate());

        Event event = findEventById(eventId);

        if (updateEventDto.getStateAction() != null) {
            if (!State.PENDING.equals(event.getState()))
                throw new DataConflictException("event not Pending");
            switch (updateEventDto.getStateAction()) {
                case "PUBLISH_EVENT":
                    event.setState(State.PUBLISHED);
                    event.setPublishedDate(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(State.CANCELED);
                    break;
            }
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventDto.getCategory()));
        }

        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }

        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventDto.getLocation()));
        }

        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }

        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));

        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));

        return EventMapper.toFullDto(event);
    }

    public List<ShortEventDto> getEventsByPublic(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, SortType sort, int from, int size, HttpServletRequest request) {

        statsService.addHit(request);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start date must be before end date");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.getAllByPublic(text, categories, paid, rangeStart, rangeEnd, pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();

        for (Event event : events) {
            eventsParticipantLimit.put(event.getId(), event.getParticipantLimit());
        }

        List<ShortEventDto> shortEventDto = events.stream()
                .map(EventMapper::toShortDto).collect(Collectors.toList());

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        for (ShortEventDto shortDto : shortEventDto) {
            shortDto.setConfirmedRequests(confirmedRequests.getOrDefault(shortDto.getId(), 0L));
            shortDto.setViews(views.getOrDefault(shortDto.getId(), 0L));
        }

        if (onlyAvailable) {
            shortEventDto = shortEventDto.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        switch (sort) {
            case EVENT_DATE:
                shortEventDto.sort(Comparator.comparing(ShortEventDto::getEventDate));
                break;
            case VIEWS:
                shortEventDto.sort(Comparator.comparing(ShortEventDto::getViews).reversed());
                break;
        }

        return shortEventDto;
    }

    public FullEventDto getEventByPublic(Long eventId, HttpServletRequest request) {

        statsService.addHit(request);

        Event event = findEventById(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event wit id=" + eventId + "already exist");
        }

        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));

        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));

        return EventMapper.toFullDto(event);
    }

    public List<ShortEventDto> getAllByPrivate(Long userId, int from, int size) {

        findUserById(userId);

        Pageable pageable = PageRequest.of(from, size);

        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        for (Event event : events) {
            event.setViews(views.getOrDefault(event.getId(), 0L));
            event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
        }

        return events.stream().map(EventMapper::toShortDto).collect(Collectors.toList());
    }

    public FullEventDto getByIdByInitiator(Long userId, Long eventId) {

        Event event = findEventById(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        Event eventByInitiator = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        Map<Long, Long> views = statsService.getViews(List.of(eventByInitiator));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(eventByInitiator));

        eventByInitiator.setViews(views.getOrDefault(eventByInitiator.getId(), 0L));
        eventByInitiator.setViews(confirmedRequests.getOrDefault(eventByInitiator.getId(), 0L));

        eventRepository.save(eventByInitiator);

        return EventMapper.toFullDto(eventByInitiator);
    }

    public List<ParticipationRequestDto> getParticipationRequestsByPrivate(Long userId, Long eventId) {
        findUserById(userId);
        findEventById(eventId);

        return participationRequestRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FullEventDto addByPrivate(Long userId, NewEventDto newEventDto) {

        checkEventDate(newEventDto.getEventDate());

        User eventUser = findUserById(userId);
        Category eventCategory = findCategoryById(newEventDto.getCategory());
        Location eventLocation = getOrSaveLocation(LocationMapper.toDto(newEventDto.getLocation()));

        Event newEvent = EventMapper.fromDto(newEventDto, eventCategory, eventUser, eventLocation, State.PENDING);

        newEvent.setViews(0L);
        newEvent.setConfirmedRequests(0L);

        return EventMapper.toFullDto(eventRepository.save(newEvent));
    }

    private void checkEventDate(LocalDateTime dateTime) {
        if (Objects.nonNull(dateTime) && LocalDateTime.now().plusHours(2).isAfter(dateTime)) {
            throw new BadRequestException("The event date must be 2 hours from the current time or later.");
        }
    }

    @Transactional
    public FullEventDto updateByPrivate(Long userId, Long eventId, UserUpdateEventDto userUpdateEventDto) {

        checkEventDate(userUpdateEventDto.getEventDate());

        findUserById(userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!(event.getState().equals(State.CANCELED) ||
                event.getState().equals(State.PENDING))) {
            throw new DataConflictException("Only pending or canceled events can be changed");
        }

        if (userUpdateEventDto.getAnnotation() != null) {
            event.setAnnotation(userUpdateEventDto.getAnnotation());
        }

        if (userUpdateEventDto.getCategory() != null) {
            event.setCategory(findCategoryById(userUpdateEventDto.getCategory()));
        }

        if (userUpdateEventDto.getDescription() != null) {
            event.setDescription(userUpdateEventDto.getDescription());
        }

        if (userUpdateEventDto.getEventDate() != null) {
            event.setEventDate(userUpdateEventDto.getEventDate());
        }

        if (userUpdateEventDto.getLocation() != null) {
            event.setLocation(getOrSaveLocation(LocationMapper.toDto(userUpdateEventDto.getLocation())));
        }

        if (userUpdateEventDto.getPaid() != null) {
            event.setPaid(userUpdateEventDto.getPaid());
        }

        if (userUpdateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(userUpdateEventDto.getParticipantLimit());
        }

        if (userUpdateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(userUpdateEventDto.getRequestModeration());
        }

        if (userUpdateEventDto.getStateAction() != null) {
            switch (userUpdateEventDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(State.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(State.CANCELED);
                    break;
            }
        }

        if (userUpdateEventDto.getTitle() != null) {
            event.setTitle(userUpdateEventDto.getTitle());
        }

        Map<Long, Long> views = statsService.getViews(List.of(event));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event));

        event.setViews(views.getOrDefault(event.getId(), 0L));
        event.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsByPrivate(Long userId,
                                                                              Long eventId,
                                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRequestRepository.countByEventIdAndStatus(eventId, CONFIRMED);

        if (confirmLimit <= 0) {
            throw new DataConflictException("The participant limit has been reached");
        }

        List<ParticipationRequest> requestList = participationRequestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        List<Long> notFoundIds = eventRequestStatusUpdateRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Participation request with id=" + notFoundIds + " was not found");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Participation request with id=" + req.getId() + " was not found");
            }

            if (confirmLimit <= 0) {
                req.setStatus(REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.toDto(req));
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    req.setStatus(Status.CONFIRMED);
                    result.getConfirmedRequests().add(ParticipationRequestMapper.toDto(req));
                    confirmLimit--;
                    break;
                case REJECTED:
                    req.setStatus(Status.REJECTED);
                    result.getRejectedRequests().add(ParticipationRequestMapper.toDto(req));
                    break;
            }
        }

        participationRequestRepository.saveAll(requestList);

        return result;
    }


    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private Category findCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }


    private Location getOrSaveLocation(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(LocationMapper.fromDto(locationDto));
    }

}
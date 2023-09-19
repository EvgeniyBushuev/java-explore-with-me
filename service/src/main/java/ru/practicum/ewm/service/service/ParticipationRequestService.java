package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.exception.DataConflictException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.model.ParticipationRequest;
import ru.practicum.ewm.service.model.User;
import ru.practicum.ewm.service.model.enus.State;
import ru.practicum.ewm.service.model.enus.Status;
import ru.practicum.ewm.service.storage.EventRepository;
import ru.practicum.ewm.service.storage.ParticipationRequestRepository;
import ru.practicum.ewm.service.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> getAll(long userId) {
        findUserById(userId);

        return participationRequestRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto add(long userId, long eventId) {
        User requester = findUserById(userId);
        Event event = findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("Event initiator cannot submit a participation request for own event");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Cannot participate in an unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            Integer confirmedRequests = participationRequestRepository.countByEventIdAndStatus(eventId, Status.CONFIRMED);
            if (event.getParticipantLimit() <= confirmedRequests) {
                throw new DataConflictException("The number of participation requests " +
                        "has exceeded the limit for the event");
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setCreatedTime(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration()
                && !event.getParticipantLimit().equals(0) ? Status.PENDING : Status.CONFIRMED);
        return ParticipationRequestMapper.toDto(participationRequestRepository.save(participationRequest));
    }

    public ParticipationRequestDto update(long userId, long requestId) {
        findUserById(userId);
        ParticipationRequest participationRequest = findParticipationRequestById(requestId);

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("No events available for editing were found");
        }

        participationRequest.setStatus(Status.CANCELED);

        return ParticipationRequestMapper.toDto(participationRequestRepository.save(participationRequest));
    }


    private ParticipationRequest findParticipationRequestById(long id) {
        return participationRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Participation request with id=" + id + " was not found"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }
}
package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.service.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.CompilationMapper;
import ru.practicum.ewm.service.model.Compilation;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.storage.CompilationRepository;
import ru.practicum.ewm.service.storage.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CompilationDto add(NewCompilationDto newDto) {

        List<Event> events = new ArrayList<>();

        if (!newDto.getEvents().isEmpty()) {
            events = eventRepository.findAllByIdIn(newDto.getEvents());
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.fromDto(newDto, events));

        return getById(compilation.getId());
    }

    @Transactional
    public CompilationDto update(Long id, UpdateCompilationDto updateDto) {

        Compilation compilation = compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Compilation with Id=" + id + " was not found"));

        if (updateDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(updateDto.getEvents()));
        }

        Optional.ofNullable(updateDto.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(updateDto.getPinned()).ifPresent(compilation::setPinned);

        return CompilationMapper.toDto(compilationRepository.save(compilation));
    }

    public CompilationDto getById(Long id) {

        return CompilationMapper.toDto(compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Compilation with Id=" + id + " was not found")));
    }

    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {

        Pageable pageable = PageRequest.of(from, size);

        return compilationRepository.findAllByPublic(pinned, pageable).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with Id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }
}
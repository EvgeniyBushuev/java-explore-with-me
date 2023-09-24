package ru.practicum.ewm.service.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.service.dto.compilation.UpdateCompilationDto;
import ru.practicum.ewm.service.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newDto) {
        log.info("Запрос на создание списка событий от ADMIN");
        return compilationService.add(newDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patch(@PathVariable long compId,
                                @Valid @RequestBody UpdateCompilationDto updateDto) {
        log.info("Запрос на изменине подборки событий от ADMIN");
        return compilationService.update(compId, updateDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long compId) {
        log.info("Запрос на удаление подборки событий от ADMIN");
        compilationService.delete(compId);
    }
}

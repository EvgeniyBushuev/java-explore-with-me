package ru.practicum.ewm.service.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.category.CategoryDto;
import ru.practicum.ewm.service.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerAdmin {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Запрос на создание категории событий {} от ADMIN", categoryDto.getName());
        return categoryService.add(categoryDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patch(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Запрос на изменение категории событий с ID {} от ADMIN", id);
        return categoryService.update(id, categoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удадление категории событий c ID {} от ADMIN", id);
        categoryService.delete(id);
    }
}

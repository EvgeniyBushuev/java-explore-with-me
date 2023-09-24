package ru.practicum.ewm.service.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.category.CategoryDto;
import ru.practicum.ewm.service.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerPublic {
    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryDto> getAll(@Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                    @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Запрос списка категорий событий от PUBLIC");
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable long id) {
        log.info("Запрос категории событий c ID {} jn PUBLIC", id);
        return categoryService.getById(id);
    }
}
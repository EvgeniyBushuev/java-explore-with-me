package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.category.CategoryDto;
import ru.practicum.ewm.service.model.Category;

public class CategoryMapper {
    public static Category fromDto(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
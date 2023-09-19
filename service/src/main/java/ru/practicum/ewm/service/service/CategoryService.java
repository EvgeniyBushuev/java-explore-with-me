package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.dto.category.CategoryDto;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.CategoryMapper;
import ru.practicum.ewm.service.storage.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDto add(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {

        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));

        categoryDto.setId(id);

        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.fromDto(categoryDto)));
    }

    public CategoryDto getById(Long id) {
        return CategoryMapper.toDto(categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category with Id=" + id + " was not found")));
    }

    public List<CategoryDto> getAll(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).getContent()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void delete(Long id) {
        categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category with Id=" + id + " was not found"));

        categoryRepository.deleteById(id);
    }
}
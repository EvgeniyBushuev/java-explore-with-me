package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.dto.user.UserDto;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.UserMapper;
import ru.practicum.ewm.service.model.User;
import ru.practicum.ewm.service.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto add(UserDto userDto) {

        return UserMapper.toDto(userRepository.save(UserMapper.fromDto(userDto)));
    }

    public User getById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    public List<UserDto> getAll(List<Long> ids, int from, int size) {

        Pageable pageable = PageRequest.of(from, size);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void delete(Long id) {

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id не существует."));

        userRepository.deleteById(id);
    }
}

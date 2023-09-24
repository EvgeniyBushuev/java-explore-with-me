package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.user.UserDto;
import ru.practicum.ewm.service.model.User;

public class UserMapper {
    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
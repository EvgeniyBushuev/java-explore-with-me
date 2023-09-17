package ru.practicum.ewm.service.mapper;


import ru.practicum.ewm.service.dto.location.LocationDto;
import ru.practicum.ewm.service.model.Location;

public class LocationMapper {
    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location fromDto(LocationDto dto) {
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }
}

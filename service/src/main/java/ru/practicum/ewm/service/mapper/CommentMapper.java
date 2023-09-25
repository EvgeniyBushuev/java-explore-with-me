package ru.practicum.ewm.service.mapper;

import ru.practicum.ewm.service.dto.comment.CommentDto;
import ru.practicum.ewm.service.model.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(UserMapper.toDto(comment.getAuthor()));
        dto.setEvent(EventMapper.toShortDto(comment.getEvent()));
        dto.setCreateDate(comment.getCreated());
        return dto;
    }
}
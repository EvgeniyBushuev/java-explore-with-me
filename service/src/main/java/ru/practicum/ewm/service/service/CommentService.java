package ru.practicum.ewm.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.service.dto.comment.CommentDto;
import ru.practicum.ewm.service.dto.comment.NewCommentDto;
import ru.practicum.ewm.service.exception.DataConflictException;
import ru.practicum.ewm.service.exception.NotFoundException;
import ru.practicum.ewm.service.mapper.CommentMapper;
import ru.practicum.ewm.service.model.Comment;
import ru.practicum.ewm.service.model.Event;
import ru.practicum.ewm.service.model.User;
import ru.practicum.ewm.service.model.enums.State;
import ru.practicum.ewm.service.storage.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment was not found id=" + commentId));
    }

    @Transactional(readOnly = true)
    public CommentDto getById(Long userId, Long commentId) {
        userService.getById(userId);
        return CommentMapper.toDto(getCommentById(commentId));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAll(Long userId, Integer from, Integer size) {
        User author = userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByAuthor(author, pageable).stream().map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto add(NewCommentDto dto, Long userId, Long eventId) {
        Event event = eventService.findEventById(eventId);
        User author = userService.getById(userId);
        if (State.PUBLISHED.equals(event.getState()))
            throw new DataConflictException("Only publish event can been commented");
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setText(dto.getText());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto update(NewCommentDto dto, Long userId, Long commentId) {
        Comment commentFromDB = getCommentById(commentId);
        User author = userService.getById(userId);
        if (!commentFromDB.getAuthor().getId().equals(author.getId()))
            throw new DataConflictException("Only comment owner or admin can been edit comment");
        commentFromDB.setText(dto.getText());
        return CommentMapper.toDto(commentFromDB);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = getCommentById(commentId);
        User author = userService.getById(userId);
        if (!comment.getAuthor().getId().equals(author.getId()))
            throw new DataConflictException("Only comment owner or admin can been delete comment");
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto updateByAdmin(Long commentId, NewCommentDto dto) {
        Comment commentFromDB = getCommentById(commentId);
        commentFromDB.setText(dto.getText());
        return CommentMapper.toDto(commentFromDB);
    }

    @Transactional
    public void deleteByAdmin(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        Event event = eventService.findEventById(eventId);
        Pageable pageable = PageRequest.of(from / size, size);
        return commentRepository.findAllByEvent(event, pageable).stream().map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
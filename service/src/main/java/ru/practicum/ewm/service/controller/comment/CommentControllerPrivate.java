package ru.practicum.ewm.service.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.comment.CommentDto;
import ru.practicum.ewm.service.dto.comment.NewCommentDto;
import ru.practicum.ewm.service.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerPrivate {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable @PositiveOrZero Long userId,
                              @PathVariable @PositiveOrZero Long commentId) {
        log.info("Запрос на получение комментария commentId={} от пользователя userId={}", commentId, userId);
        return commentService.getById(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getAllUserComments(
            @PathVariable Long userId,
            @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
            @Valid @RequestParam(defaultValue = "10") @Min(10) int size
    ) {
        log.info("Запрос на списка комментариев от пользователя userId={}", userId);
        return commentService.getAll(userId, from, size);
    }

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@RequestBody @Valid NewCommentDto dto,
                             @PathVariable @PositiveOrZero Long userId,
                             @PathVariable @PositiveOrZero Long eventId) {
        log.info("Запрос на добавление комментария о событии eventId={} от пользователя userId={}, data={}", eventId, userId, dto);
        return commentService.add(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patch(@RequestBody @Valid NewCommentDto dto,
                            @PathVariable @PositiveOrZero Long userId,
                            @PathVariable @PositiveOrZero Long commentId) {
        log.info("Запрос на обновление комментария commentId={} от пользователя userId={}", commentId, userId);
        return commentService.update(dto, userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Запрос на удаление комментария commentId={} от пользователя userId={}", commentId, userId);
        commentService.delete(userId, commentId);
    }
}
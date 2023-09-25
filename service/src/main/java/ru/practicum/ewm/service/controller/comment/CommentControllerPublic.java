package ru.practicum.ewm.service.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.dto.comment.CommentDto;
import ru.practicum.ewm.service.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerPublic {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentsForEvent(@PathVariable @PositiveOrZero Long eventId,
                                                   @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @Valid @RequestParam(defaultValue = "10") @Min(10) int size
    ) {
        log.info("Запрос на получение всех комментариев события eventId={}", eventId);
        return commentService.getAllCommentsByEvent(eventId, from, size);
    }
}
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
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
public class CommentControllerAdmin {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto renewalCommentAdmin(@RequestBody @Valid NewCommentDto dto,
                                          @PathVariable @PositiveOrZero Long commentId) {
        log.info("Запрос на обновление комментария commentID={} администратором", commentId);
        return commentService.updateByAdmin(commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAdmin(@PathVariable @PositiveOrZero Long commentId) {
        log.info("Запрос на удаление комментария commentId={} администратором", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
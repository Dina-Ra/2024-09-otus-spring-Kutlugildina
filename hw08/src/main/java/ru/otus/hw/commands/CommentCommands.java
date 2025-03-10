package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;


@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Find comment by book id", key = "cbbid")
    public String findCommentByBookId(String bookId) {
        return commentService.findByBookId(bookId)
                .stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    // cins BookCommentary_1 1
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, String bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    // cupd 1 EditBookCommentary_1 1
    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String commentId, String text, String bookId) {
        var savedComment = commentService.update(commentId, text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    // cdel 1
    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(String commentId) {
        commentService.deleteById(commentId);
    }
}

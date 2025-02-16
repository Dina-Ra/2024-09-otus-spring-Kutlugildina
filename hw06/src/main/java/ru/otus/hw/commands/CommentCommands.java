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
    public String findCommentById(Long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find comment by book id", key = "cbbid")
    public String findCommentByBookId(Long bookId) {
        return commentService.findByBookId(bookId)
                .stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    // cins BookCommentary_1 1
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertBook(String text, Long bookId) {
        var savedComment = commentService.save(text, bookId);
        return commentConverter.commentToString(savedComment);
    }
}

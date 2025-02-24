package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(Long id);

    List<CommentDto> findByBookId(Long bookId);

    CommentDto insert(String text, Long bookId);

    CommentDto update(Long id, String text, Long bookId);

    void deleteById(Long id);
}

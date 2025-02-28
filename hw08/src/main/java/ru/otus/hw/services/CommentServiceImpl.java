package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<CommentDto> findById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id cannot be empty string");
        }

        var commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("Comment by id [%s] not found".formatted(id));
        }

        var comment = commentOptional.get();
        return Optional.of(new CommentDto(comment.getId(), comment.getText()));
    }

    @Override
    public List<CommentDto> findByBookId(String bookId) {
        var bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Comments find by book_id [%s] is field. Book is not found".formatted(bookId));
        }
        return commentRepository.findByBookId(bookId).stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getText()))
                .toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Transactional
    @Override
    public CommentDto update(String commentId, String text, String bookId) {
        if (!StringUtils.isNumeric(commentId)) {
            throw new IllegalArgumentException("commentId cannot be empty string or not numeric");
        }
        return save(commentId, text, bookId);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(String id, String text, String bookId) {
        if (StringUtils.isBlank(bookId)) {
            throw new IllegalArgumentException("bookId cannot be empty string");
        }

        var bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book by id [%s] not found".formatted(bookId));
        }

        var comment = commentRepository.save(new Comment(id, text, bookOptional.get()));
        return new CommentDto(comment.getId(), comment.getText());
    }
}

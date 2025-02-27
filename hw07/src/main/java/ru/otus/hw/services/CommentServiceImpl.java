package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
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
    public Optional<CommentDto> findById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id cannot be less zero");
        }

        var commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("Comment by id [%d] not found".formatted(id));
        }

        var comment = commentOptional.get();
        return Optional.of(new CommentDto(comment.getId(), comment.getText()));
    }

    @Override
    public List<CommentDto> findByBookId(Long bookId) {
        var bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Comments find by book_id [%d] is field. Book is not found".formatted(bookId));
        }
        return commentRepository.findByBookId(bookId).stream()
                .map(comment -> new CommentDto(comment.getId(), comment.getText()))
                .toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String text, Long bookId) {
        return save(null, text, bookId);
    }

    @Transactional
    @Override
    public CommentDto update(Long id, String text, Long bookId) {
        return save(id, text, bookId);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private CommentDto save(Long id, String text, Long bookId) {
        if (bookId <= 0) {
            throw new IllegalArgumentException("bookId cannot be less zero");
        }

        var bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new IllegalArgumentException("Book by id [%d] not found".formatted(bookId));
        }

        var comment = commentRepository.save(new Comment(id, text, bookOptional.get()));
        return new CommentDto(comment.getId(), comment.getText());
    }
}

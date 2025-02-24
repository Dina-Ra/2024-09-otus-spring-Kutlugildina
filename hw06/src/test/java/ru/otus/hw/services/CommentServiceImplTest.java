package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;
import ru.otus.hw.repositories.JpaGenreRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с комментариями ")
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({JpaAuthorRepository.class, JpaGenreRepository.class, JpaBookRepository.class,
        JpaCommentRepository.class, CommentServiceImpl.class, BookServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CommentServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var commentDto = commentService.insert("BookCommentary_1", 1L);
        assertThat(commentDto).isNotNull();

        var expectedCommentDto = commentService.findById(commentDto.id());

        assertThat(expectedCommentDto)
                .isPresent()
                .get()
                .matches(commentDto::equals)
                .isNotNull();
    }

    @DisplayName("должен обновить комментарий по id")
    @Test
    void shouldUpdateCommentById() {
        var insertCommentDto = commentService.insert("BookCommentary_1", 1L);
        assertThat(insertCommentDto).isNotNull();

        var updateCommentDto = commentService.update(insertCommentDto.id(), "EditBookCommentary_1", 1L);
        assertThat(updateCommentDto).isNotNull();

        var expectedCommentDto = commentService.findById(updateCommentDto.id());

        assertThat(expectedCommentDto)
                .isPresent()
                .get()
                .matches(updateCommentDto::equals)
                .isNotNull();
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var commentDto = commentService.insert("BookCommentary_1", 1L);
        assertThat(commentDto).isNotNull();

        var expectedCommentDto = commentService.findById(commentDto.id());

        assertThat(expectedCommentDto).isPresent()
                .get()
                .isEqualTo(commentDto);
    }

    @DisplayName("должен загружать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var bookDto = bookService.findById(2L);
        assertThat(bookDto).isPresent().isNotNull();

        var commentDto = commentService.insert("BookCommentary_5", bookDto.get().id());
        assertThat(commentDto).isNotNull();

        var commentList = commentService.findByBookId(bookDto.get().id());

        assertThat(commentList).isNotEmpty();
    }

    @DisplayName("должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        var commentDto = commentService.insert("BookCommentary_1", 3L);
        assertThat(commentDto).isNotNull();

        commentService.deleteById(commentDto.id());

        var throwable = assertThrows(IllegalArgumentException.class,
                () -> commentService.findById(commentDto.id()));
        assertThat(throwable).isNotNull();
    }
}

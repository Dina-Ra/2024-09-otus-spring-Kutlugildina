package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.events.CommentModelListener;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Сервис для работы с комментариями ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        BookRepository.class, CommentRepository.class,
                        CommentServiceImpl.class, BookServiceImpl.class,
                        SequenceGeneratorService.class, CommentModelListener.class
                }
        ),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {AuthorRepository.class, GenreRepository.class}
        ))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class IntegrationCommentServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var commentDto = commentService.insert("BookCommentary_1", "1");
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
        var insertCommentDto = commentService.insert("BookCommentary_1", "1");
        assertThat(insertCommentDto).isNotNull();
        System.out.println(commentService.findById("1"));
        var updateCommentDto = commentService.update(insertCommentDto.id(), "EditBookCommentary_1", "1");
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
        var commentDto = commentService.insert("BookCommentary_1", "1");
        assertThat(commentDto).isNotNull();

        var expectedCommentDto = commentService.findById(commentDto.id());

        assertThat(expectedCommentDto).isPresent()
                .get()
                .isEqualTo(commentDto);
    }

    @DisplayName("должен загружать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var bookDto = bookService.findById("1");
        assertThat(bookDto).isPresent().isNotNull();

        var commentDto = commentService.insert("BookCommentary_5", bookDto.get().id());
        assertThat(commentDto).isNotNull();

        var commentList = commentService.findByBookId(bookDto.get().id());

        assertThat(commentList).isNotEmpty();
    }

    @DisplayName("должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        var commentDto = commentService.insert("BookCommentary_1", "1");
        assertThat(commentDto).isNotNull();

        commentService.deleteById(commentDto.id());

        var throwable = assertThrows(IllegalArgumentException.class,
                () -> commentService.findById(commentDto.id()));
        assertThat(throwable).isNotNull();
    }
}

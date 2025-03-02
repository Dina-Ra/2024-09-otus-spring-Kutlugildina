package ru.otus.hw.services;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
@DisplayName("Сервис для работы с книгами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        BookRepository.class, CommentRepository.class,
                        CommentServiceImpl.class, BookServiceImpl.class,
                        AuthorRepository.class, GenreRepository.class
                }
        ))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class IntegrationBookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;


    @DisplayName("должен загрузить книгу по id")
    @ParameterizedTest
    @MethodSource("getDbIdBooks")
    void shouldReturnCorrectBookById(String bookId, List<CommentDto> commentDtoList) {
        var bookDtoOptional = bookService.findById(bookId);

        assertThat(bookDtoOptional).isPresent()
                .map(BookDto::id)
                .isPresent()
                .get()
                .isEqualTo(bookId);

        var expectedCommentDtoList = commentService.findByBookId(bookId);
        assertThat(expectedCommentDtoList)
                .isEqualTo(commentDtoList);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var bookDtoList = bookService.findAll();

        assertThat(bookDtoList.stream()
                .filter(bookDto -> ObjectUtils.isEmpty(bookDto.genreDtoList()))
                .findFirst()
        )
                .isEmpty();

        assertThat(bookDtoList.stream()
                .filter(bookDto -> Objects.isNull(bookDto.authorDto()))
                .findFirst()
        )
                .isEmpty();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var bookDto = bookService.insert("newTitle", "67c49395d3a28750b0cca8fr",
                Set.of("67c49395d3a28750b0cca8fn", "67c49395d3a28750b0cca8fa"));
        assertThat(bookDto.authorDto())
                .isNotNull();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookDto = bookService.update("67c49395d3a28750b0cca8ff","editTitle", "67c49395d3a28750b0cca8fq",
                Set.of("67c49395d3a28750b0cca8fn", "67c49395d3a28750b0cca8fb"));

        assertThat(bookDto).isNotNull()
                .matches(book -> ObjectUtils.isNotEmpty(book.genreDtoList()))
                .isNotNull();

        assertThat(bookDto).isNotNull()
                .matches(book -> Objects.nonNull(book.authorDto()))
                .isNotNull();
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        bookService.deleteById("67c49395d3a28750b0cca8fe");
        var throwable = assertThrows(IllegalArgumentException.class,
                () -> commentService.findByBookId("67c49395d3a28750b0cca8fe"));
        assertThat(throwable)
                .message()
                .isEqualTo("Comments find by book_id [67c49395d3a28750b0cca8fe] is field. Book is not found");
    }

    private static Stream<Arguments> getDbIdBooks() {
        var commentDto1Book3 = new CommentDto("67c49395d3a28750b0cca8fk", "Text_1");
        var commentDto2Book3 = new CommentDto("67c49395d3a28750b0cca8fl", "Text_2");
        var commentDto3Book3 = new CommentDto("67c49395d3a28750b0cca8fm", "Text_3");
        return Stream.of(
                Arguments.of("67c49395d3a28750b0cca8ff", Collections.emptyList()),
                Arguments.of("67c49395d3a28750b0cca8feg", new ArrayList<>(List.of(commentDto1Book3, commentDto2Book3, commentDto3Book3)))
        );
    }
}

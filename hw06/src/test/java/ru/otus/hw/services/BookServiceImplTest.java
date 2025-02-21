package ru.otus.hw.services;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.repositories.JpaAuthorRepository;
import ru.otus.hw.repositories.JpaGenreRepository;
import ru.otus.hw.repositories.JpaBookRepository;
import ru.otus.hw.repositories.JpaCommentRepository;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("Сервис на для работы с книгами ")
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({JpaAuthorRepository.class, JpaGenreRepository.class, JpaBookRepository.class,
        JpaCommentRepository.class, CommentServiceImpl.class, BookServiceImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;


    @DisplayName("должен загрузить книгу по id")
    @ParameterizedTest
    @MethodSource("getDbIdBooks")
    void shouldReturnCorrectBookById(Long bookId, List<CommentDto> commentDtoList) {
        Optional<BookDto> bookDtoOptional = bookService.findById(bookId);

        assertThat(bookDtoOptional).isPresent()
                .map(BookDto::id)
                .isPresent()
                .get()
                .isEqualTo(bookId);

        List<CommentDto> expectedCommentDtoList = commentService.findByBookId(bookId);
        assertThat(expectedCommentDtoList)
                .isEqualTo(commentDtoList);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<BookDto> bookDtoList = bookService.findAll();

        assertThat(bookDtoList.stream()
                .filter(bookDto -> CollectionUtils.isEmpty(bookDto.genreDtoList()))
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
        var bookDto = bookService.insert("newTitle", 2L, Set.of(2L, 3L));
        assertThat(bookDto.authorDto())
                .isNotNull();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookDto = bookService.update(1L,"editTitle", 2L, Set.of(2L, 3L));

        assertThat(bookDto).isNotNull()
                .matches(book -> CollectionUtils.isNotEmpty(book.genreDtoList()))
                .isNotNull();

        assertThat(bookDto).isNotNull()
                .matches(book -> Objects.nonNull(book.authorDto()))
                .isNotNull();
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        bookService.deleteById(1L);
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> commentService.findByBookId(1L));
        assertThat(throwable)
                .message()
                .isEqualTo("Comments find by book_id [1] is field. Book is not found");
    }

    private static Stream<Arguments> getDbIdBooks() {
        CommentDto commentDto1Book3 = new CommentDto(4L, "BookCommentary_1");
        CommentDto commentDto2Book3 = new CommentDto(5L, "BookCommentary_2");
        CommentDto commentDto3Book3 = new CommentDto(6L, "BookCommentary_3");
        return Stream.of(
                Arguments.of(2L, Collections.emptyList()),
                Arguments.of(3L, new ArrayList<>(List.of(commentDto1Book3, commentDto2Book3, commentDto3Book3)))
        );
    }
}

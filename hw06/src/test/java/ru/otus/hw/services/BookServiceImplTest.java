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
import ru.otus.hw.exceptions.EntityNotFoundException;
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
        try {
            Optional<BookDto> bookDto1 = bookService.findById(bookId);
            assertThat(bookDto1).isPresent()
                    .map(BookDto::commentDtoList)
                    .isPresent()
                    .get()
                    .isEqualTo(commentDtoList);
        } catch (EntityNotFoundException e) {
            assertThat(e.getMessage()).isNotNull().isEqualTo("Find Book by id [%d] is field".formatted(bookId));
        }
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<BookDto> bookDtoList = bookService.findAll();

        assertThat(bookDtoList.stream()
                .filter(bookDto -> bookDto.commentDtoList().stream()
                        .anyMatch(commentDto -> Objects.deepEquals(commentDto.text(), "BookCommentary_3")))
                .findFirst()
        )
                .isPresent();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var bookDto = bookService.insert("newTitle", 2L, Set.of(2L, 3L));
        assertThat(bookDto).isNotNull()
                .matches(book -> CollectionUtils.isEmpty(book.commentDtoList()))
                .isNotNull();
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var bookDto = bookService.update(1L,"editTitle", 2L, Set.of(2L, 3L));

        assertThat(bookDto).isNotNull()
                .matches(book -> CollectionUtils.isNotEmpty(book.commentDtoList()))
                .isNotNull();
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        bookService.deleteById(1L);
        List<CommentDto> commentDtoList = commentService.findByBookId(1L);
        assertThat(commentDtoList).isEmpty();
    }

    private static Stream<Arguments> getDbIdBooks() {
        CommentDto commentDto1Book1 = new CommentDto(1L, "BookCommentary_1");
        CommentDto commentDto2Book1 = new CommentDto(2L, "BookCommentary_2");
        CommentDto commentDto3Book1 = new CommentDto(3L, "BookCommentary_3");
        CommentDto commentDto1Book3 = new CommentDto(4L, "BookCommentary_1");
        CommentDto commentDto2Book3 = new CommentDto(5L, "BookCommentary_2");
        CommentDto commentDto3Book3 = new CommentDto(6L, "BookCommentary_3");
        return Stream.of(
                Arguments.of(1L, new ArrayList<>(List.of(commentDto1Book1, commentDto2Book1, commentDto3Book1))),
                Arguments.of(2L, Collections.emptyList()),
                Arguments.of(3L, new ArrayList<>(List.of(commentDto1Book3, commentDto2Book3, commentDto3Book3)))
        );
    }
}

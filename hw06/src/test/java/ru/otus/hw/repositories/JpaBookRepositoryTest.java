package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с книгами ")
@DataJpaTest
@Import({JpaBookRepository.class})
class JpaBookRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaBookRepository jpaBookRepository;


    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbIdBooks")
    void shouldReturnCorrectBookById(Long expectedIdBook) {
        var actualBook = jpaBookRepository.findById(expectedIdBook);
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(testEntityManager.find(Book.class, expectedIdBook));
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = jpaBookRepository.findAll().stream()
                .collect(Collectors.groupingBy(objectArray -> String.valueOf(((Book) objectArray[0]).getId())));

        var expectedBooks = LongStream.range(1, 4).boxed()
                .map(expectedIdBook -> testEntityManager.find(Book.class, expectedIdBook))
                .toList();

        assertThat(actualBooks.size()).isEqualTo(expectedBooks.size());
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = testEntityManager.find(Author.class, 1);
        var genres1 = testEntityManager.find(Genre.class, 1);
        var genres3 = testEntityManager.find(Genre.class, 3);
        var expectedBook = new Book(null, "BookTitle_10500", author,
                List.of(genres1, genres3));

        var returnedBook = jpaBookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(testEntityManager.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = testEntityManager.find(Author.class, 3);
        var genres5 = testEntityManager.find(Genre.class, 5);
        var genres6 = testEntityManager.find(Genre.class, 6);
        var expectedBook = new Book(1L, "BookTitle_10500", author,
                List.of(genres5, genres6));

        assertThat(jpaBookRepository.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = jpaBookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(testEntityManager.find(Book.class, returnedBook.getId())).isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(testEntityManager.find(Book.class, 1L)).isNotNull();
        jpaBookRepository.deleteById(1L);
        assertThat(testEntityManager.find(Book.class, 1L)).isNull();
    }

    private static List<Long> getDbIdBooks() {
        return LongStream.range(1, 4).boxed()
                .toList();
    }
}
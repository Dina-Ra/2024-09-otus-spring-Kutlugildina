package ru.otus.hw.repositories;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = BookRepository.class),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {AuthorRepository.class, GenreRepository.class, CommentRepository.class}
        ))
class MongoBookRepositoryTest {

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private BookRepository bookRepository;


    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbIdBooks")
    void shouldReturnCorrectBookById(String expectedIdBook) {
        var actualBook = bookRepository.findById(expectedIdBook);
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(mongoOperations.findById(expectedIdBook, Book.class));
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();

        var expectedBooks = actualBooks.stream()
                .map(Book::getId)
                .map(expectedIdBook -> mongoOperations.findById(expectedIdBook, Book.class))
                .toList();

        assertThat(actualBooks.size()).isEqualTo(expectedBooks.size());
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = mongoOperations.findById("67c49395d3a28750b0cca8fr", Author.class);
        var genres1 = mongoOperations.findById("67c49395d3a28750b0cca8fo", Genre.class);
        var genres3 = mongoOperations.findById("67c49395d3a28750b0cca8fa", Genre.class);
        assertThat(author).isNotNull();
        assertThat(genres1).isNotNull();
        assertThat(genres3).isNotNull();

        var expectedBook = new Book(null, "BookTitle_10500", author,
                List.of(genres1, genres3));

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> StringUtils.isNotBlank(book.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(mongoOperations.findById(returnedBook.getId(), Book.class)).isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = mongoOperations.findById("67c49395d3a28750b0cca8fp", Author.class);
        var genres5 = mongoOperations.findById("67c49395d3a28750b0cca8fc", Genre.class);
        var genres6 = mongoOperations.findById("67c49395d3a28750b0cca8fd", Genre.class);
        assertThat(author).isNotNull();
        assertThat(genres5).isNotNull();
        assertThat(genres6).isNotNull();

        var expectedBook = new Book("67c49395d3a28750b0cca8fe", "BookTitle_10500", author,
                List.of(genres5, genres6));

        assertThat(mongoOperations.findById(expectedBook.getId(), Book.class))
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> StringUtils.isNotBlank(book.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(mongoOperations.findById(returnedBook.getId(), Book.class)).isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        bookRepository.deleteById("67c49395d3a28750b0cca8feg");
        assertThat(mongoOperations.findById("67c49395d3a28750b0cca8feg", Book.class)).isNull();
    }

    private static List<String> getDbIdBooks() {
        return Stream.of("67c49395d3a28750b0cca8fe", "67c49395d3a28750b0cca8ff")
                .map(String::valueOf)
                .toList();
    }
}
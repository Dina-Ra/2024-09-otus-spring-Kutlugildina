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
import ru.otus.hw.events.BookModelListener;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {BookRepository.class, SequenceGeneratorService.class, BookModelListener.class}),
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
        var author = mongoOperations.findById("1", Author.class);
        var genres1 = mongoOperations.findById("1", Genre.class);
        var genres3 = mongoOperations.findById("3", Genre.class);
        assertThat(author).isNotNull();
        assertThat(genres1).isNotNull();
        assertThat(genres3).isNotNull();

        var expectedBook = new Book(null, "BookTitle_10500", author,
                List.of(genres1, genres3));

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> StringUtils.isNumeric(book.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(mongoOperations.findById(returnedBook.getId(), Book.class)).isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = mongoOperations.findById("3", Author.class);
        var genres5 = mongoOperations.findById("5", Genre.class);
        var genres6 = mongoOperations.findById("6", Genre.class);
        assertThat(author).isNotNull();
        assertThat(genres5).isNotNull();
        assertThat(genres6).isNotNull();

        var expectedBook = new Book("1", "BookTitle_10500", author,
                List.of(genres5, genres6));

        assertThat(mongoOperations.findById(expectedBook.getId(), Book.class))
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> StringUtils.isNumeric(book.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(mongoOperations.findById(returnedBook.getId(), Book.class)).isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        bookRepository.deleteById("1");
        assertThat(mongoOperations.findById("1", Book.class)).isNull();
    }

    private static List<String> getDbIdBooks() {
        return LongStream.range(2, 4).boxed()
                .map(String::valueOf)
                .toList();
    }
}
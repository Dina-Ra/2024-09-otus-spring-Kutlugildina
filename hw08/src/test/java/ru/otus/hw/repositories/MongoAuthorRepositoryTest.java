package ru.otus.hw.repositories;

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
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с авторами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {AuthorRepository.class, SequenceGeneratorService.class}),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {BookRepository.class, GenreRepository.class, CommentRepository.class}
        ))
public class MongoAuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MongoOperations mongoOperations;


    @DisplayName("должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getDbIdAuthors")
    void shouldReturnCorrectAuthorById(String expectedIdAuthor) {
        var actualAuthor = authorRepository.findById(expectedIdAuthor);
        assertThat(actualAuthor).isPresent()
                .get()
                .isEqualTo(mongoOperations.findById(expectedIdAuthor, Author.class));
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthor = authorRepository.findAll();
        var expectedAuthor = LongStream.range(1, 4).boxed()
                .map(String::valueOf)
                .map(expectedIdAuthor -> mongoOperations.findById(expectedIdAuthor, Author.class))
                .toList();

        assertThat(actualAuthor).containsExactlyElementsOf(expectedAuthor);
        actualAuthor.forEach(System.out::println);
    }

    private static List<String> getDbIdAuthors() {
        return LongStream.range(1, 4).boxed()
                .map(String::valueOf)
                .toList();
    }
}

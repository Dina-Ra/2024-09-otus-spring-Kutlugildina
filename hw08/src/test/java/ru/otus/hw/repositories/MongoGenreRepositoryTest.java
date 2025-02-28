package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.models.Genre;
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {GenreRepository.class, SequenceGeneratorService.class}),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {BookRepository.class, AuthorRepository.class, CommentRepository.class}
        ))
public class MongoGenreRepositoryTest {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenreList = genreRepository.findAll();
        var expectedGenreList = LongStream.range(1, 7).boxed()
                .map(String::valueOf)
                .map(expectedIdGenre -> mongoOperations.findById(expectedIdGenre, Genre.class))
                .toList();

        assertThat(actualGenreList).containsExactlyElementsOf(expectedGenreList);
    }

    @DisplayName("должен загружать список жанров по множеству id")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var actualGenreList = genreRepository.findAllByIds(getLongStream().collect(Collectors.toSet()));

        var expectedGenreList = getLongStream()
                .map(idGenre -> mongoOperations.findById(idGenre, Genre.class))
                .toList();

        assertThat(actualGenreList).containsExactlyElementsOf(expectedGenreList);
    }

    private Stream<String> getLongStream() {
        return LongStream.of(1, 2, 4).boxed()
                .map(String::valueOf);
    }
}

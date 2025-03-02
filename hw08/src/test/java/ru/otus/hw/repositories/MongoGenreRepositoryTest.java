package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = GenreRepository.class),
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
        var expectedGenreList = actualGenreList.stream()
                .map(expectedIdGenre -> mongoOperations.findById(expectedIdGenre.getId(), Genre.class))
                .toList();

        assertThat(actualGenreList).containsExactlyElementsOf(expectedGenreList);
    }
}

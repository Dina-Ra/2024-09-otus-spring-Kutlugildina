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
import ru.otus.hw.models.Genre;

import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с жанрами ")
@DataJpaTest
@Import({JpaGenreRepository.class})
public class JpaGenreRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaGenreRepository jpaGenreRepository;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenreList() {
        var actualGenreList = jpaGenreRepository.findAll();
        var expectedGenreList = LongStream.range(1, 7).boxed()
                .map(expectedIdGenre -> testEntityManager.find(Genre.class, expectedIdGenre))
                .toList();

        assertThat(actualGenreList).containsExactlyElementsOf(expectedGenreList);
    }

    @DisplayName("должен загружать список жанров по множеству id")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var actualGenreList = jpaGenreRepository.findAllByIds(getLongStream().collect(Collectors.toSet()));

        var expectedGenreList = getLongStream()
                .map(idGenre -> testEntityManager.find(Genre.class, idGenre))
                .toList();

        assertThat(actualGenreList).containsExactlyElementsOf(expectedGenreList);
    }

    private Stream<Long> getLongStream() {
        return LongStream.of(1, 2, 4).boxed();
    }
}

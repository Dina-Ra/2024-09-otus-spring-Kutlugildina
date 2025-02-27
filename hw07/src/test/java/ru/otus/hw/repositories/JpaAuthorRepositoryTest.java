package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с авторами ")
@DataJpaTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = AuthorRepository.class),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {BookRepository.class, GenreRepository.class, CommentRepository.class}
        ))
public class JpaAuthorRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("должен загружать автора по id")
    @ParameterizedTest
    @MethodSource("getDbIdAuthors")
    void shouldReturnCorrectAuthorById(Long expectedIdAuthor) {
        var actualAuthor = authorRepository.findById(expectedIdAuthor);
        assertThat(actualAuthor).isPresent()
                .get()
                .isEqualTo(testEntityManager.find(Author.class, expectedIdAuthor));
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthor = authorRepository.findAll();
        var expectedAuthor = LongStream.range(1, 4).boxed()
                .map(expectedIdAuthor -> testEntityManager.find(Author.class, expectedIdAuthor))
                .toList();

        assertThat(actualAuthor).containsExactlyElementsOf(expectedAuthor);
        actualAuthor.forEach(System.out::println);
    }

    private static List<Long> getDbIdAuthors() {
        return LongStream.range(1, 4).boxed()
                .toList();
    }
}

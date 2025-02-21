package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями ")
@DataJpaTest
@Import({JpaCommentRepository.class})
public class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = testEntityManager.find(Book.class, 1L);
        var expectedComment = new Comment(null, "BookCommentary_1", book);

        var returnedComment = jpaCommentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(testEntityManager.find(Comment.class, returnedComment.getId())).isEqualTo(returnedComment);
    }

    @DisplayName("должен обновить комментарий по id")
    @Test
    void shouldUpdateCommentById() {
        var book = testEntityManager.find(Book.class, 1L);
        var expectedComment = new Comment(1L, "EditBookCommentary_1", book);

        var returnedComment = jpaCommentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(testEntityManager.find(Comment.class, returnedComment.getId())).isEqualTo(returnedComment);
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expectedComment = testEntityManager.find(Comment.class, 1L);

        var actualComment = jpaCommentRepository.findById(1L);

        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var actualCommentList = jpaCommentRepository.findByBookId(1L);

        var expectedCommentList = actualCommentList.stream()
                .map(Comment::getId)
                .map(idComment -> testEntityManager.find(Comment.class, idComment))
                .toList();

        assertThat(actualCommentList).containsExactlyElementsOf(expectedCommentList);
    }

    @DisplayName("должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        var comment = testEntityManager.find(Comment.class, 1L);
        jpaCommentRepository.deleteById(comment.getId());

        var expectedComment = testEntityManager.find(Comment.class, comment.getId());
        assertThat(expectedComment).isNull();
    }
}

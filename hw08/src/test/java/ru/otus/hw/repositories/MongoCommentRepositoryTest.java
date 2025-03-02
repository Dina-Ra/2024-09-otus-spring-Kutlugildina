package ru.otus.hw.repositories;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с комментариями ")
@DataMongoTest(
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = CommentRepository.class),
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {BookRepository.class, GenreRepository.class, CommentRepository.class}
        ))
public class MongoCommentRepositoryTest {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = mongoOperations.findById("67c49395d3a28750b0cca8fe", Book.class);
        var expectedComment = new Comment(null, "BookCommentary_1", book);

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> StringUtils.isNotBlank(comment.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(mongoOperations.findById(returnedComment.getId(), Comment.class)).isEqualTo(returnedComment);
    }

    @DisplayName("должен обновить комментарий по id")
    @Test
    void shouldUpdateCommentById() {
        var book = mongoOperations.findById("67c49395d3a28750b0cca8fe", Book.class);
        var expectedComment = new Comment("67c49395d3a28750b0cca8fh", "EditBookCommentary_1", book);

        var returnedComment = commentRepository.save(expectedComment);

        assertThat(returnedComment).isNotNull()
                .matches(comment -> StringUtils.isNotBlank(comment.getId()))
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(mongoOperations.findById(returnedComment.getId(), Comment.class)).isEqualTo(returnedComment);
    }

    @DisplayName("должен загружать комментарий по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expectedComment = mongoOperations.findById("67c49395d3a28750b0cca8fi", Comment.class);

        var actualComment = commentRepository.findById("67c49395d3a28750b0cca8fi");

        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список комментариев по id книги")
    @Test
    void shouldReturnCorrectCommentByBookId() {
        var actualCommentList = commentRepository.findByBookId("67c49395d3a28750b0cca8feg");

        var expectedCommentList = actualCommentList.stream()
                .map(Comment::getId)
                .map(idComment -> mongoOperations.findById(idComment, Comment.class))
                .toList();

        assertThat(actualCommentList).containsExactlyElementsOf(expectedCommentList);
    }

    @DisplayName("должен удалить комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        commentRepository.deleteById("67c49395d3a28750b0cca8fm");

        var expectedComment = mongoOperations.findById("67c49395d3a28750b0cca8fm", Comment.class);
        assertThat(expectedComment).isNull();
    }
}

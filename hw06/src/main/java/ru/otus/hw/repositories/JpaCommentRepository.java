package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;


@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        var query = entityManager.createQuery(
                "select e from Comment e where e.book.id=:bookId",
                Comment.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        var commentClone = comment.clone();

        if (isNull(commentClone.getId())) {
            entityManager.persist(commentClone);
            comment.setId(commentClone.getId());
        } else {
            comment = entityManager.merge(commentClone);
        }
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        var comment = entityManager.find(Comment.class, id);
        entityManager.remove(comment);
    }
}

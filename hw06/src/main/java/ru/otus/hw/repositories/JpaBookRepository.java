package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.Map;
import java.util.Optional;
import java.util.List;

import static java.util.Objects.isNull;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(Long id) {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("author-entity-graph");
        Map<String, Object> properties = Map.of(FETCH.getKey(), entityGraph);

        return Optional.ofNullable(entityManager.find(Book.class, id, properties));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<?> entityGraph = entityManager.getEntityGraph("author-entity-graph");
        var query = entityManager.createQuery(
                "select e from Book e",
                Book.class);
        query.setHint(FETCH.getKey(), entityGraph);

        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        var bookClone = book.clone();
        if (isNull(book.getId())) {
            entityManager.persist(bookClone);
            book.setId(bookClone.getId());
        } else {
            book = entityManager.merge(bookClone);
        }

        return book;
    }

    @Override
    public void deleteById(Long id) {
        var book = entityManager.find(Book.class, id);
        entityManager.remove(book);
    }
}

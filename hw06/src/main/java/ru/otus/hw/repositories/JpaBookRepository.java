package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.Optional;
import java.util.List;

import static java.util.Objects.isNull;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<Book> findById(Long id) {
        var query = entityManager.createQuery(
                "select distinct e from Book e left join fetch e.author where e.id=:id",
                Book.class);
        query.setParameter("id", id);
        try {
            var book = query.getSingleResult();
            if (isNull(book)) {
                return Optional.empty();
            }

            return Optional.of(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Object[]> findAll() {
       var query = entityManager.createQuery("select distinct e,c from Book e " +
               "left join fetch e.author " +
               "left join fetch Comment c on c.book.id=e.id");
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

        entityManager.flush();
        return book;
    }

    @Override
    public void deleteById(Long id) {
        var book = entityManager.find(Book.class, id);
        entityManager.remove(book);
    }
}

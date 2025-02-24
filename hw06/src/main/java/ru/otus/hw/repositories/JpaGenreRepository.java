package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaGenreRepository implements GenreRepository {

    private final EntityManager entityManager;

    @Override
    public List<Genre> findAll() {
        var query = entityManager.createQuery("select e from Genre e", Genre.class);
        return query.getResultList();
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var query = entityManager.createQuery("select e from Genre e where id in (:ids)", Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}

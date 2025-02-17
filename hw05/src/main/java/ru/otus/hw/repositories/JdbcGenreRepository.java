package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query("select id, name from genres", new GenreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        var parameters = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcOperations.query(
                "select id, name from genres where id in (:ids)",
                parameters,
                new GenreRowMapper()
        );
    }

    private static class GenreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            var id = resultSet.getLong("id");
            var name = resultSet.getString("name");
            return new Genre(id, name);
        }
    }
}

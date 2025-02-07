package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        var query = "select books.id, books.title, authors.id, authors.full_name, genres.id, genres.name from books " +
                "inner join authors on authors.id = books.author_id " +
                "inner join books_genres on books_genres.book_id = books.id " +
                "inner join genres on genres.id = books_genres.genre_id " +
                "where books.id=:id";

        var parameters = new MapSqlParameterSource("id", id);

        var book = namedParameterJdbcOperations.query(query, parameters, new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var parameters = new MapSqlParameterSource("id", id);
        namedParameterJdbcOperations
                .update("delete from books where id=:id", parameters);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations
                .query("select books.id, books.title, authors.id, authors.full_name from books inner join authors on authors.id = books.author_id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations
                .query("select book_id, genre_id from books_genres", new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> bookMap = booksWithoutGenres.stream()
                .collect(Collectors.toMap(
                        book -> book.getId(),
                        book -> book
                ));
        Map<Long, Genre> genreMap = genres.stream()
                .collect(Collectors.toMap(
                        genre -> genre.getId(),
                        genre -> genre
                ));

        relations.stream()
                .collect(Collectors.groupingBy(
                                BookGenreRelation::bookId,
                                Collectors.mapping(BookGenreRelation::genreId, Collectors.toList())
                        )
                )
                .forEach((bookId, genreIdList) -> {
                    List<Genre> genreList = genreMap.entrySet().stream()
                            .filter(entry -> genreIdList.contains(entry.getKey()))
                            .map(Map.Entry::getValue).toList();

                    Book book = bookMap.get(bookId);
                    if (Objects.nonNull(book)) {
                        book.setGenres(genreList);
                    }
                });
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        var parameters = new MapSqlParameterSource(
                Map.of("title", book.getTitle(), "author_id", book.getAuthor().getId())
        );
        var row = namedParameterJdbcOperations
                .update("insert into books (title, author_id) values (:title, :author_id)", parameters, keyHolder, new String[]{"id"});

        if (row == 0) {
            throw new EntityNotFoundException("insert book with title = %s and author_id = %s is field".formatted(book.getTitle(), book.getAuthor()));
        }

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var parameters = new MapSqlParameterSource(
                Map.of("id", book.getId(), "title", book.getTitle(), "author_id", book.getAuthor().getId())
        );

        var row = namedParameterJdbcOperations
                .update("update books set title = :title, author_id = :author_id where id = :id", parameters);
        if (row == 0) {
            throw new EntityNotFoundException("update book with id = %s is field".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var bookGenreRelationList = book.getGenres()
                .stream()
                .map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                .toList();

        var parametersArray = SqlParameterSourceUtils.createBatch(bookGenreRelationList);

        var rowArrays = namedParameterJdbcOperations
                .batchUpdate("insert into books_genres (book_id, genre_id) values (:bookId, :genreId)", parametersArray);

        if (rowArrays.length == 0) {
            throw new EntityNotFoundException("insert genres relations for book with book_id = %s is field".formatted(book.getId()));
        }
    }

    private void removeGenresRelationsFor(Book book) {
        var bookId = book.getId();
        var parameters = new MapSqlParameterSource("book_id", bookId);
        var rows = namedParameterJdbcOperations
                .update("delete from books_genres where book_id=:book_id", parameters);
        if (rows == 0) {
            throw new EntityNotFoundException("remove genres relations for book with book_id = %s is field".formatted(bookId));
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            var author = new Author();
            author.setId(resultSet.getLong("authors.id"));
            author.setFullName(resultSet.getString("authors.full_name"));

            var id = resultSet.getLong("books.id");
            var title = resultSet.getString("books.title");

            return new Book(id, title, author, new ArrayList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Book book = null;
            List<Genre> genres = new ArrayList<>();
            while (resultSet.next()) {
                if (Objects.isNull(book)) {
                    var author = new Author();
                    author.setId(resultSet.getLong("authors.id"));
                    author.setFullName(resultSet.getString("authors.full_name"));

                    book = new Book();
                    book.setId(resultSet.getLong("books.id"));
                    book.setTitle(resultSet.getString("books.title"));
                    book.setAuthor(author);
                    book.setGenres(genres);
                }

                var genre = new Genre();
                genre.setId(resultSet.getLong("genres.id"));
                genre.setName(resultSet.getString("genres.name"));
                genres.add(genre);
            }

            if (Objects.nonNull(book)) {
                book.setGenres(genres);
            }

            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            var book_id = resultSet.getLong("book_id");
            var genre_id = resultSet.getLong("genre_id");
            return new BookGenreRelation(book_id, genre_id);
        }
    }
}

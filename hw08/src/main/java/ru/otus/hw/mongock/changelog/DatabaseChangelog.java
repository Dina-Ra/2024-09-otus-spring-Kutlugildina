package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.sequencegenerator.SequenceGeneratorService;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "dinara", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insert", author = "dinara")
    public void insert(AuthorRepository authorRepository,
                       GenreRepository genreRepository,
                       BookRepository bookRepository,
                       SequenceGeneratorService sequenceGeneratorService) {

        var author1 = authorRepository.insert(new Author("1", "Author_1"));
        var author2 = authorRepository.insert(new Author("2", "Author_2"));
        var author3 = authorRepository.insert(new Author("3", "Author_3"));

        var genre1 = genreRepository.insert(new Genre("1", "Genre_1"));
        var genre2 = genreRepository.insert(new Genre("2", "Genre_2"));
        var genre3 = genreRepository.insert(new Genre("3", "Genre_3"));
        var genre4 = genreRepository.insert(new Genre("4", "Genre_4"));
        var genre5 = genreRepository.insert(new Genre("5", "Genre_5"));
        var genre6 = genreRepository.insert(new Genre("6", "Genre_6"));

        bookRepository.insert(new Book(
                sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME),
                "BookTitle_1", author1, List.of(genre1, genre2)));
        bookRepository.insert(new Book(
                sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME),
                "BookTitle_2", author2, List.of(genre3, genre4)));
        bookRepository.insert(new Book(
                sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME),
                "BookTitle_3", author3, List.of(genre5, genre6)));
    }
}

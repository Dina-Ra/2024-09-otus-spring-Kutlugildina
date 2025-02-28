package ru.otus.hw.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
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
                       CommentRepository commentRepository,
                       SequenceGeneratorService sequenceGeneratorService) {
        Author author1 = authorRepository.insert(new Author("1", "Author_1"));
        Author author2 = authorRepository.insert(new Author("2", "Author_2"));
        Author author3 = authorRepository.insert(new Author("3", "Author_3"));

        Genre genre1 = genreRepository.insert(new Genre("1", "Genre_1"));
        Genre genre2 = genreRepository.insert(new Genre("2", "Genre_2"));
        Genre genre3 = genreRepository.insert(new Genre("3", "Genre_3"));
        Genre genre4 = genreRepository.insert(new Genre("4", "Genre_4"));
        Genre genre5 = genreRepository.insert(new Genre("5", "Genre_5"));
        Genre genre6 = genreRepository.insert(new Genre("6", "Genre_6"));

        var book1 = bookRepository.insert(new Book(sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME), "BookTitle_1", author1, List.of(genre1, genre2)));
        bookRepository.insert(new Book(sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME), "BookTitle_2", author2, List.of(genre3, genre4)));
        var book3 = bookRepository.insert(new Book(sequenceGeneratorService.generateSequence(Book.SEQUENCE_NAME), "BookTitle_3", author3, List.of(genre5, genre6)));

        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_1", book1));
        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_2", book1));
        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_3", book1));
        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_1", book3));
        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_2", book3));
        commentRepository.insert(new Comment(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME), "Text_3", book3));
    }
}

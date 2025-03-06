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
                       CommentRepository commentRepository) {
        Author author1 = authorRepository.insert(new Author("67c49395d3a28750b0cca8fr", "Author_1"));
        Author author2 = authorRepository.insert(new Author("67c49395d3a28750b0cca8fq", "Author_2"));
        Author author3 = authorRepository.insert(new Author("67c49395d3a28750b0cca8fp", "Author_3"));

        Genre genre1 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fo", "Genre_1"));
        Genre genre2 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fn", "Genre_2"));
        Genre genre3 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fa", "Genre_3"));
        Genre genre4 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fb", "Genre_4"));
        Genre genre5 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fc", "Genre_5"));
        Genre genre6 = genreRepository.insert(new Genre("67c49395d3a28750b0cca8fd", "Genre_6"));

        var book1 = bookRepository.insert(new Book("67c49395d3a28750b0cca8fe", "BookTitle_1", author1, List.of(genre1, genre2)));
        bookRepository.insert(new Book("67c49395d3a28750b0cca8ff", "BookTitle_2", author2, List.of(genre3, genre4)));
        var book3 = bookRepository.insert(new Book("67c49395d3a28750b0cca8feg", "BookTitle_3", author3, List.of(genre5, genre6)));

        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fh", "Text_1", book1));
        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fi", "Text_2", book1));
        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fj", "Text_3", book1));
        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fk", "Text_1", book3));
        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fl", "Text_2", book3));
        commentRepository.insert(new Comment("67c49395d3a28750b0cca8fm", "Text_3", book3));
    }
}

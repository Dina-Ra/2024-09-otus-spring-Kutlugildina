package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Optional;
import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<BookDto> findById(String id) {
        var bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            throw new EntityNotFoundException("Find Book by id [%s] is field".formatted(id));
        }

        var book = bookOptional.get();
        return Optional.of(generateBookDto(book));
    }

    @Override
    public List<BookDto> findAll() {
        var bookList = bookRepository.findAll();

        return bookList.stream()
                .map(this::generateBookDto)
                .toList();
    }

    @Override
    public BookDto insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    public BookDto update(String id, String title, String authorId, Set<String> genresIds) {
        if (!StringUtils.isNumeric(id)) {
            throw new IllegalArgumentException("book id cannot be empty string or not numeric");
        }
        return save(id, title, authorId, genresIds);
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    private BookDto save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var authorOptional = authorRepository.findById(authorId);
        if (authorOptional.isEmpty()) {
            throw new EntityNotFoundException("Author with id %s not found".formatted(authorId));
        }
        Author author = authorOptional.get();

        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = bookRepository.save(new Book(id, title, author, genres));
        return generateBookDto(book);
    }

    private BookDto generateBookDto(Book book) {
        var authorDto = new AuthorDto(book.getAuthor().getId(), book.getAuthor().getFullName());

        var genres = book.getGenres()
                .stream()
                .map(genre -> new GenreDto(genre.getId(), genre.getName()))
                .toList();

        return new BookDto(book.getId(), book.getTitle(), authorDto, genres);
    }
}

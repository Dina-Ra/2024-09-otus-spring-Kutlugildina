package ru.otus.hw.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("books")
public class Book {

    @Id
    private String id;

    private String title;

    private Author author;

    private List<Genre> genres;

    @Override
    public Book clone() {
        Book book = new Book();
        book.setId(this.id);
        book.setTitle(this.title);

        Author author = new Author();
        if (nonNull(this.author)) {
            author.setId(this.author.getId());
            author.setFullName(this.author.getFullName());
            book.setAuthor(author);
        }

        List<Genre> genreList;
        if (isNotEmpty(this.genres)) {
            genreList = new ArrayList<>();
            this.genres.forEach(genre -> {
                Genre genreClone = new Genre();
                genreClone.setId(genre.getId());
                genreClone.setName(genre.getName());
                genreList.add(genreClone);
            });
            book.setGenres(genreList);
        }
        return book;
    }
}

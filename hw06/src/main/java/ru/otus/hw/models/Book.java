package ru.otus.hw.models;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"genres", "author"})
@ToString(exclude = {"genres", "author"})
@Entity
@Table(name = "books")
@NamedEntityGraph(name = "author-entity-graph",
        attributeNodes = {@NamedAttributeNode("author")})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Author author;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "books_genres", joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
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
        if (CollectionUtils.isNotEmpty(this.genres)) {
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

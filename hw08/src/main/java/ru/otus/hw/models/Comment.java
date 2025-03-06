package ru.otus.hw.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


import static java.util.Objects.nonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("comments")
public class Comment {

    @Id
    private String id;

    private String text;

    private Book book;

    @Override
    public Comment clone() {
        Comment comment = new Comment();
        comment.setId(this.id);
        comment.setText(this.text);

        if (nonNull(this.book)) {
            Book book = this.book.clone();
            comment.setBook(book);
        }

        return comment;
    }
}

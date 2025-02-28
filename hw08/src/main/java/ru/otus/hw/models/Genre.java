package ru.otus.hw.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("genres")
public class Genre {
    @Transient
    public static final String SEQUENCE_NAME = "genres_sequence";

    @Id
    private String id;

    private String name;
}

package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@Component
public class CommentConverter {

    public String commentToString(CommentDto commentDto) {
        return "Id: %d, Text: %s".formatted(commentDto.id(), commentDto.text());
    }
}

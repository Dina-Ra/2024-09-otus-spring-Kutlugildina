package ru.otus.hw.dto;

import java.util.List;

public record BookDto(long id,
                      String title,
                      AuthorDto authorDto,
                      List<GenreDto> genreDtoList) {
}

package ru.otus.hw.dto;

import java.util.List;

public record BookDto(String id,
                      String title,
                      AuthorDto authorDto,
                      List<GenreDto> genreDtoList) {
}

package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {
        System.out.println(authorRepository.findAll());
        return authorRepository.findAll().stream()
                .map(author -> new AuthorDto(author.getId(), author.getFullName()))
                .toList();
    }
}

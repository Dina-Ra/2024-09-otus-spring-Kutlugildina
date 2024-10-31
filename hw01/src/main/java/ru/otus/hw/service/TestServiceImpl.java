package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        questionDao.findAll()
                .forEach(question -> {
                    ioService.printLine(question.text());
                    ioService.printLine(question.answers().stream()
                            .map(Answer::text)
                            .map(text -> " - " + text)
                            .collect(Collectors.joining(System.lineSeparator())));
                });
    }
}

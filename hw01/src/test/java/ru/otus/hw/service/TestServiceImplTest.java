package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static ru.otus.hw.service.TestServiceImpl.FORMAT_SPECIFIER;

public class TestServiceImplTest {

    private final IOService ioService = mock(StreamsIOService.class);

    private final QuestionDao questionDao = mock(CsvQuestionDao.class);

    private final TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);

    @BeforeEach
    void setUp() {
        doNothing().when(ioService).printFormattedLine(isA(String.class), isA(Object[].class));
        doNothing().when(ioService).printLine(isA(String.class));
    }

    @DisplayName("an empty list of questions has been printed")
    @Test
    void emptyListQuestionsPrinted() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());

        testService.executeTest();

        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
    }

    @DisplayName("list of one question has been printed")
    @Test
    void listOfOneQuestionPrinted() {
        var answer1 = new Answer("answer1", false);
        var answer2 = new Answer("answer2", true);
        var answer3 = new Answer("answer3", false);
        var answerList = List.of(answer1, answer2, answer3);
        var question = new Question("question", answerList);

        given(questionDao.findAll()).willReturn(List.of(question));

        testService.executeTest();

        verify(ioService, times(1)).printLine(question.text());
        verify(ioService, times(1)).printFormattedLine(
                FORMAT_SPECIFIER.repeat(answerList.size()), answerList.stream().map(Answer::text).toArray()
        );
    }
}

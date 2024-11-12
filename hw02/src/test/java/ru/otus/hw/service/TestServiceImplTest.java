package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static ru.otus.hw.service.TestServiceImpl.*;

public class TestServiceImplTest {

    private final IOService ioService = mock(StreamsIOService.class);

    private final QuestionDao questionDao = mock(CsvQuestionDao.class);

    private final TestServiceImpl testService = new TestServiceImpl(ioService, questionDao);

    @BeforeEach
    void setUp() {
        doNothing().when(ioService).printFormattedLine(isA(String.class), isA(Object[].class));
        doNothing().when(ioService).printLine(isA(String.class));
    }

    @DisplayName("empty list of questions")
    @Test
    void emptyListQuestions() {
        Student student = new Student("Firstname", "Lastname");
        given(questionDao.findAll()).willReturn(Collections.emptyList());

        TestResult testResult = testService.executeTestFor(student);

        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");

        assertEquals(student, testResult.getStudent());
        assertEquals(0, testResult.getRightAnswersCount());
        assertEquals(Collections.emptyList(), testResult.getAnsweredQuestions());
    }

    @DisplayName("list of one question")
    @ParameterizedTest
    @CsvSource({"1,0", "3,0", "2,1"})
    void listOfOneQuestion(int answerNumber, int rightAnswersCount) {
        var student = new Student("Firstname", "Lastname");

        var answer1 = new Answer("answer1", false);
        var answer2 = new Answer("answer2", true);
        var answer3 = new Answer("answer3", false);
        var answerList = List.of(answer1, answer2, answer3);
        var question = new Question("question", answerList);
        var questionList = List.of(question);

        given(questionDao.findAll()).willReturn(questionList);
        given(ioService.readIntForRangeWithPrompt(1, answerList.size(), String.format(PROMPT, 3), String.format(ERROR_MESSAGE, 3)))
                .willReturn(answerNumber);

        var testResult = testService.executeTestFor(student);

        assertEquals(student, testResult.getStudent());
        assertEquals(rightAnswersCount, testResult.getRightAnswersCount());
        assertEquals(questionList, testResult.getAnsweredQuestions());

        verify(ioService, times(1)).printLine(question.text());
        verify(ioService, times(1)).printLine(
                String.format("1. %s%n2. %s%n3. %s%n", answerList.stream().map(Answer::text).toArray())
        );
    }

    @DisplayName("one question and thrown exception while reading int")
    @Test
    void oneQuestionList_throwExceptionReadingInt() {
        var student = new Student("Firstname", "Lastname");

        var answer1 = new Answer("answer1", false);
        var answer2 = new Answer("answer2", true);
        var answerList = List.of(answer1, answer2);
        var question = new Question("question", answerList);
        var questionList = List.of(question);

        given(questionDao.findAll()).willReturn(questionList);
        given(ioService.readIntForRangeWithPrompt(1, answerList.size(), String.format(PROMPT, 2), String.format(ERROR_MESSAGE, 2)))
                .willThrow(IllegalArgumentException.class);

        var testResult = testService.executeTestFor(student);

        assertEquals(student, testResult.getStudent());
        assertEquals(0, testResult.getRightAnswersCount());
        assertEquals(questionList, testResult.getAnsweredQuestions());

        verify(ioService, times(1)).printLine(question.text());
        verify(ioService, times(1)).printLine(
                String.format("1. %s%n2. %s%n", answerList.stream().map(Answer::text).toArray())
        );
    }
}

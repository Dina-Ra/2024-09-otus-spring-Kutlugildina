package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    protected static final String PROMPT = "---- Enter a number from 1 to %s ----";

    protected static final String ERROR_MESSAGE = "You entered NOT NUMBER or NUMBER OUTSIDE the range [1 - %s]";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = getIsCorrectAnswerQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean getIsCorrectAnswerQuestion(Question question) {
        var answers = question.answers();

        var answersLine = IntStream.range(0, answers.size())
                .boxed()
                .map(i -> "%d. %s%n".formatted(i + 1, answers.get(i).text()))
                .collect(Collectors.joining());

        ioService.printLine(question.text());
        ioService.printLine(answersLine);

        try {
            int answerNumber = getReadInt(answers);
            return answers.get(answerNumber - 1).isCorrect();
        } catch (IllegalArgumentException e) {
            ioService.printFormattedLine("All attempts have been used%n");
            return false;
        }
    }

    private int getReadInt(List<Answer> answers) {
        var answerSize = answers.size();
        var prompt = String.format(PROMPT, answerSize);
        var errorMessage = String.format(ERROR_MESSAGE, answerSize);
        return ioService.readIntForRangeWithPrompt(1, answerSize, prompt, errorMessage);
    }
}

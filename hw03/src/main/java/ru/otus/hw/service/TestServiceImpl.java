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

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

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
            ioService.printFormattedLineLocalized("TestService.all.attempts.used", "%n");
            return false;
        }
    }

    private int getReadInt(List<Answer> answers) {
        var answerSize = answers.size();
        var prompt = ioService.getMessage("TestService.enter.number.prompt", answerSize);
        var errorMessage = ioService.getMessage("TestService.enter.number.error.message", answerSize);
        return ioService.readIntForRangeWithPrompt(1, answerSize, prompt, errorMessage);
    }
}

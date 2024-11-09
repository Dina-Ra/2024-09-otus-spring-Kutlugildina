package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    protected static final String FORMAT_SPECIFIER = ". %s%n";

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
            if (CollectionUtils.isEmpty(question.answers())) {
                break;
            }
            var isAnswerValid = getIsCorrectAnswerQuestion(question); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean getIsCorrectAnswerQuestion(Question question) {
        var answers = question.answers();

        var answersTextArray = getQuestionAnswersTextToArray(question);
        var format = getFormatSpecifier(answers);

        ioService.printLine(question.text());
        ioService.printFormattedLine(format, answersTextArray);

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

    private Object[] getQuestionAnswersTextToArray(Question question) {
        return question.answers()
                .stream()
                .map(Answer::text)
                .toArray(Object[]::new);
    }

    private String getFormatSpecifier(List<Answer> answerList) {
        var builderSpecifier = new StringBuilder();
        for (int i = 1; i <= answerList.size() ; i++) {
            builderSpecifier.append(i);
            builderSpecifier.append(FORMAT_SPECIFIER);
        }
        return builderSpecifier.toString();
    }
}

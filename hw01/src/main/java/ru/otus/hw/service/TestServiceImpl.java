package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;


@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    protected static final String FORMAT_SPECIFIER = " - %s%n";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questionList = questionDao.findAll();
        questionList.forEach(this::printQuestion);
    }

    private void printQuestion(Question question) {
        var answersTextArray = getQuestionAnswersTextToArray(question);
        var format = FORMAT_SPECIFIER.repeat(answersTextArray.length);

        ioService.printLine(question.text());
        ioService.printFormattedLine(format, answersTextArray);
    }

    private Object[] getQuestionAnswersTextToArray(Question question) {
        return question.answers()
                .stream()
                .map(Answer::text)
                .toArray(Object[]::new);
    }
}

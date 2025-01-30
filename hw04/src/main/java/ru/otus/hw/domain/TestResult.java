package ru.otus.hw.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestResult {

    private final List<Question> answeredQuestions;

    private int rightAnswersCount;

    public TestResult() {
        this.answeredQuestions = new ArrayList<>();
    }

    public void applyAnswer(Question question, boolean isRightAnswer) {
        answeredQuestions.add(question);
        if (isRightAnswer) {
            rightAnswersCount++;
        }
    }
}

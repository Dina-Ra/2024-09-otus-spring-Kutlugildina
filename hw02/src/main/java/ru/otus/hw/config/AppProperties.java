package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppProperties implements TestConfig, TestFileNameProvider {

    // внедрить свойство из application.properties
    private final int rightAnswersCountToPass;

    // внедрить свойство из application.properties
    private final String testFileName;

    public AppProperties(@Value(value = "${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
                         @Value(value = "${test.fileName}") String testFileName) {
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
    }
}

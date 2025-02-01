package ru.otus.hw.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private QuestionDao questionDao;

    @DisplayName("need to get questions with answers to them")
    @Test
    void needToGetQuestionsWithAnswer() {
        var testFileName = "questions.csv";
        given(fileNameProvider.getTestFileName()).willReturn(testFileName);

        List<Question> questions = questionDao.findAll();

        verify(fileNameProvider, times(1)).getTestFileName();
        assertEquals(3, questions.size());
        assertFalse(CollectionUtils.isEmpty(questions.get(2).answers()));
    }

    @DisplayName("throwable exception file not found")
    @Test
    void throwableExceptionFileNotFound() {
        var testFileName = "any.csv";
        given(fileNameProvider.getTestFileName()).willReturn(testFileName);

        Throwable throwable = assertThrows(QuestionReadException.class, questionDao::findAll);
        assertEquals("file [any.csv] not found!", throwable.getMessage());
    }
}

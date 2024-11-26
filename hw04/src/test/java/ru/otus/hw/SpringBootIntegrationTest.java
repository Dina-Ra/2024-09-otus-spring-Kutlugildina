package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"test.locale=en-US"})
public class SpringBootIntegrationTest {

    @Autowired
    private TestService testService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AppProperties appProperties;

    @MockBean
    private LocalizedIOService ioService;

    @BeforeEach
    void setUp() {
        doNothing().when(ioService).printLineLocalized(isA(String.class));
        doNothing().when(ioService).printLine(isA(String.class));
        doNothing().when(ioService).printFormattedLineLocalized(isA(String.class));
        given(ioService.getMessage(isA(String.class), isA(Integer.class))).willReturn("");
        given(ioService.readIntForRangeWithPrompt(eq(1), isA(Integer.class), eq(""), eq("")))
                .willReturn(1);
    }

    @Test
    @Order(1)
    public void test() {
        given(ioService.readStringWithPromptLocalized(eq("StudentService.input.first.name"))).willReturn("FirstName");
        given(ioService.readStringWithPromptLocalized(eq("StudentService.input.last.name"))).willReturn("LastName");
        given(ioService.getMessage(eq("StudentService.determine.current.student.success"), isA(String.class)))
                .willReturn("Welcome: FirstName LastName");

        var determineCurrentStudentResult = studentService.determineCurrentStudentResult();
        assertEquals("Welcome: FirstName LastName", determineCurrentStudentResult);

        var student = studentService.getCurrentStudent();
        assertEquals("FirstName LastName", student.getFullName());

        var testResult = testService.executeTestFor(student);
        assertEquals(student, testResult.getStudent());
        assertEquals(2, testResult.getRightAnswersCount());
    }

    @Test
    @Order(2)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void test2() {
        given(ioService.readStringWithPromptLocalized(eq("StudentService.input.first.name"))).willReturn("FirstName");
        given(ioService.readStringWithPromptLocalized(eq("StudentService.input.last.name"))).willReturn("");
        given(ioService.getMessage(eq("StudentService.determine.current.student.field")))
                .willReturn("Input your full name");

        var determineCurrentStudentResult = studentService.determineCurrentStudentResult();
        assertEquals("Input your full name", determineCurrentStudentResult);

        verify(ioService, times(0)).getMessage(eq("StudentService.determine.current.student.success"), isA(String.class));

        var student = studentService.getCurrentStudent();
        assertNull(student);
    }

    @Test
    public void test3() {
        assertEquals(Locale.US, appProperties.getLocale());
        assertEquals("questions.csv", appProperties.getTestFileName());
    }
}

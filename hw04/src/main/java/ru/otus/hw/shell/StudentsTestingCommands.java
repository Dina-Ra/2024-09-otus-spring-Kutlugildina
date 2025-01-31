package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestService;

@Order
@ShellComponent(value = "Students Testing Commands")
@RequiredArgsConstructor
public class StudentsTestingCommands {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    @ShellMethod(value = "Input name", key = {"n", "name"})
    public String determineCurrentStudent() {
        return studentService.determineCurrentStudentResult();
    }

    @ShellMethod(value = "Answer the questions", key = {"a", "answer"})
    @ShellMethodAvailability(value = "isAnswerTheQuestionsCommandAvailable")
    public void answerTheQuestions() {
        var testResult = testService.executeTestFor();
        resultService.showResult(testResult);
    }

    private Availability isAnswerTheQuestionsCommandAvailable() {
        String isDetermineStudentResult = studentService.isDetermineStudentResult();
        return StringUtils.isBlank(isDetermineStudentResult)
                ? Availability.available()
                : Availability.unavailable(isDetermineStudentResult);
    }
}

package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private Student student;

    private final LocalizedIOService ioService;

    @Override
    public String determineCurrentStudentResult() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            student = new Student(firstName, lastName);
            return ioService.getMessage("StudentService.determine.current.student.success", student.getFullName());
        }
        return ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public String isDetermineStudentResult() {
        return Objects.nonNull(student) ?
                StringUtils.EMPTY : ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public Student getCurrentStudent() {
        return student;
    }
}

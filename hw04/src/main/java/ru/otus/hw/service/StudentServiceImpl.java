package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private String fullName;

    private final LocalizedIOService ioService;

    @Override
    public String determineCurrentStudentResult() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            this.fullName = String.format("%s %s", firstName, lastName);

            return ioService.getMessage("StudentService.determine.current.student.success", this.fullName);
        }
        return ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public String isDetermineStudentResult() {
        return Objects.nonNull(this.fullName) ?
                StringUtils.EMPTY : ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public String getCurrentStudentFullName() {
        return this.fullName;
    }
}

package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.security.LoginContext;


@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final LocalizedIOService ioService;

    private final LoginContext loginContext;

    @Override
    public String determineCurrentStudentResult() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");

        if (StringUtils.isNotBlank(firstName) && StringUtils.isNotBlank(lastName)) {
            String fullName = String.format("%s %s", firstName, lastName);
            loginContext.login(fullName);

            return ioService.getMessage("StudentService.determine.current.student.success", fullName);
        }
        return ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public String isDetermineStudentResult() {
        return loginContext.isUserLoggedIn() ?
                StringUtils.EMPTY : ioService.getMessage("StudentService.determine.current.student.field");
    }

    @Override
    public String getCurrentStudentFullName() {
        return loginContext.getFullName();
    }
}

package ru.otus.hw.security;

import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class InMemoryLoginContext implements LoginContext {
    private String fullName;

    @Override
    public void login(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public boolean isUserLoggedIn() {
        return nonNull(fullName);
    }
}

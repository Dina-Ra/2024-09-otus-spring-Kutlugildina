package ru.otus.hw.security;

public interface LoginContext {
    void login(String fullName);

    String getFullName();

    boolean isUserLoggedIn();
}

package com.example.stipendi.util.contract;

public interface ErrorHandler {
    void addError(String errorMessage);
    boolean hasErrors();
    void displayErrors();
}

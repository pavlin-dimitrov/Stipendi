package com.example.stipendi.util.implementation;

import com.example.stipendi.util.contract.ErrorHandler;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandlerImpl implements ErrorHandler {
    private final List<String> errorMessages;
    private final TextArea errorTextArea;

    public ErrorHandlerImpl(TextArea errorTextArea) {
        this.errorMessages = new ArrayList<>();
        this.errorTextArea = errorTextArea;
    }

    @Override
    public void addError(String errorMessage) {
        errorMessages.add(errorMessage);
        Platform.runLater(() -> errorTextArea.appendText(errorMessage + "\n"));
    }

    @Override
    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }

    @Override
    public void displayErrors() {
        if (hasErrors()) {
            Platform.runLater(() -> {
                errorTextArea.appendText("Открити грешки по време на импортирането на файла:\n");
                errorMessages.forEach(error -> errorTextArea.appendText(error + "\n"));
            });
        } else {
            Platform.runLater(() -> errorTextArea.appendText("Не са открити грешки по време на импортирането на файла.\n"));
        }
    }

    @Override
    public void clearErrors() {
        errorMessages.clear();
        errorTextArea.clear();
    }
}
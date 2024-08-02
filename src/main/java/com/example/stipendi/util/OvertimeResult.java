package com.example.stipendi.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OvertimeResult {
    private final int overtimeHours;
    private final List<String> errors;

    public OvertimeResult(int overtimeHours) {
        this.overtimeHours = overtimeHours;
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
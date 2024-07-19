package com.example.stipendi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppConfigVariable {
    private int id;
    private String variableName;
    private double variableValue;
}

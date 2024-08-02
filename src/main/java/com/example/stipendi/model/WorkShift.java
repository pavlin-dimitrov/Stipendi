package com.example.stipendi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
public class WorkShift {
    private int id;
    private String type;
    private LocalTime startTime;
    private LocalTime endTime;
    private int regularHours;
}
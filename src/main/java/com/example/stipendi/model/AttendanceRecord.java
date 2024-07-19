package com.example.stipendi.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AttendanceRecord {
    private int id;
    private String egn;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private WorkShift workShift;
    private double regularHours;
    private double overtimeHours;
    private double totalHours;
}

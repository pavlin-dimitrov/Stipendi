package com.example.stipendi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private int id;
    private String egn;
    private String fullName;
    private City city;
    private Occupation occupation;
    private double baseSalary;
    private double professionalExperienceRate;
    private double professionalExperienceBonus;
    private double achievementBonus;
    private double oneTimeBonus;
    private double transportBonus;
    private double fixedBonus;
    private String otherConditions;
    private double daysOffDoo;
    private double daysOffEmpl;

    private int totalOvertimeWeek;
    private int totalOvertimeWeekend;
    private int totalWorkingDays;
    private double finalSalary;
}
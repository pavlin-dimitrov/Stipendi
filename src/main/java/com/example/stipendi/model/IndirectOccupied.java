package com.example.stipendi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndirectOccupied {

    private int id;
    private int year;
    private int month;
    private double hours;
}
package com.example.stipendi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class City {
    private int id;
    private String cityName;
    private double distance; // Разстоянието до фирмата
}
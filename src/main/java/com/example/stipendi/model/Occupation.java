package com.example.stipendi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Occupation {
    private int id;
    private String department;
    private String position;
    private String nkpd;
}
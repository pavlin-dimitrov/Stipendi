package com.example.stipendi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

    private int id;
    private int monthNum;
    private int holidayNum;
}

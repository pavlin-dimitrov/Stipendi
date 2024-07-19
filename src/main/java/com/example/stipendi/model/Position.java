package com.example.stipendi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Position {
    private int id;
    private String positionName;
    private String nkpdCode;
}

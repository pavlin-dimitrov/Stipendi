package com.example.stipendi.excel;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelReader {

    protected static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue()); // Преобразуване на числовата стойност в стринг
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getRichStringCellValue().getString();
                    case NUMERIC:
                        return String.valueOf((long) cell.getNumericCellValue()); // Преобразуване на числовата стойност в стринг
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    protected static double parseDuration(String duration) {
        String[] parts = duration.split(":");
        double hours = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]) / 60;
        return hours + minutes;
    }
}
package com.example.stipendi.excel;

import com.example.stipendi.model.City;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.Occupation;
import com.example.stipendi.util.ExcelFileUtil;
import com.example.stipendi.util.contract.ErrorHandler;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeExcelReader extends ExcelReader {

    public static List<Employee> readEmployeesFromExcel(String filePath, ErrorHandler errorHandler) {
        List<Employee> employeeList = new ArrayList<>();

        try (Workbook workbook = ExcelFileUtil.getWorkbook(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропускаме заглавния ред

                Employee employee = new Employee();
                readEmployeeFromRow(row, employee, errorHandler);
                employeeList.add(employee);
            }
        } catch (IOException | IllegalArgumentException e) {
            errorHandler.addError(e.getMessage());
        }

        return employeeList;
    }

    private static void readEmployeeFromRow(Row row, Employee employee, ErrorHandler errorHandler) {

        employee.setFullName(getStringCellValue(row.getCell(0)));
        employee.setEgn(getEgnCellValue(row.getCell(1), row.getRowNum(), errorHandler));
        employee.setFixedBonus(getNumericCellValue(row.getCell(2), "fixed bonus", row.getRowNum(), errorHandler));
        employee.setCity(getCityFromCell(row.getCell(3)));
        employee.setOtherConditions(getStringCellValue(row.getCell(4)));
        employee.setOccupation(getOccupationFromCell(row.getCell(5)));
        employee.setDaysOffDoo(getNumericCellValue(row.getCell(6), "Sick days by DOO", row.getRowNum(), errorHandler));
        employee.setDaysOffEmpl(getNumericCellValue(row.getCell(7), "Sick days by employer", row.getRowNum(), errorHandler));
        employee.setBaseSalary(getNumericCellValue(row.getCell(8), "base salary", row.getRowNum(), errorHandler));
        employee.setProfessionalExperienceRate(getNumericCellValue(row.getCell(9), "professional experience rate", row.getRowNum(), errorHandler));

    }

    private static String getEgnCellValue(Cell cell, int rowNum, ErrorHandler errorHandler) {
        if (cell == null) return "";

        try {
            return formatEgn(cell.getStringCellValue());
        } catch (IllegalStateException e) {
            try {
                return formatEgn(String.valueOf((long) cell.getNumericCellValue()));
            } catch (NumberFormatException | IllegalStateException ex) {
                errorHandler.addError("Невалиден формат ЕГН на ред " + (rowNum + 1));
                return null;
            }
        }
    }

    private static City getCityFromCell(Cell cell) {
        City city = new City();
        city.setCityName(getStringCellValue(cell));
        return city;
    }

    private static Occupation getOccupationFromCell(Cell cell) {
        Occupation occupation = new Occupation();
        occupation.setNkpd(getStringCellValue(cell));
        return occupation;
    }

    private static double getNumericCellValue(Cell cell, String fieldName, int rowNum, ErrorHandler errorHandler) {
        if (cell == null) {
            errorHandler.addError("Липсва " + fieldName + " на ред " + (rowNum + 1));
            return 0;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    // Премахваме празните пространства и проверяваме дали стойността е числова
                    String stringValue = cell.getStringCellValue().trim();
                    if (stringValue.isEmpty()) {
                        return 0; // Ако стойността е празна след премахване на пространствата, връщаме 0
                    } else if (isNumeric(stringValue)) {
                        return Double.parseDouble(stringValue);
                    } else {
                        errorHandler.addError("Невалиден " + fieldName + " формат на ред " + (rowNum + 1) + ": \"" + cell.getStringCellValue() + "\" (STRING)");
                        return 0;
                    }
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            return cell.getNumericCellValue();
                        case STRING:
                            String formulaValue = cell.getRichStringCellValue().getString().trim();
                            if (formulaValue.isEmpty()) {
                                return 0; // Ако стойността е празна след премахване на пространствата, връщаме 0
                            } else if (isNumeric(formulaValue)) {
                                return Double.parseDouble(formulaValue);
                            } else {
                                errorHandler.addError("Невалиден " + fieldName + " формат на ред " + (rowNum + 1) + ": \"" + cell.getRichStringCellValue().getString() + "\" (FORMULA STRING)");
                                return 0;
                            }
                        default:
                            errorHandler.addError("Неочакван тип за резултата на формула за " + fieldName + " на ред " + (rowNum + 1));
                            return 0;
                    }
                default:
                    errorHandler.addError("Неочакван тип стойност в клетката за " + fieldName + " на ред " + (rowNum + 1) + ": " + cell.getCellType());
                    return 0;
            }
        } catch (IllegalStateException e) {
            errorHandler.addError("Illegal state exception for " + fieldName + " at row " + (rowNum + 1) + ": " + e.getMessage());
            return 0;
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static String formatEgn(String egn) {
        if (egn.length() == 9) {
            egn = "0" + egn;
        }
        return egn;
    }
}
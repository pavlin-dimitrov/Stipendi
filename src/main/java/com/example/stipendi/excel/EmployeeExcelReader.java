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
        } catch (IOException e) {
            errorHandler.addError("IOException: " + e.getMessage());
        }

        return employeeList;
    }

    private static void readEmployeeFromRow(Row row, Employee employee, ErrorHandler errorHandler) {
        employee.setFullName(getStringCellValue(row.getCell(0)));
        employee.setEgn(getEgnCellValue(row.getCell(1), row.getRowNum(), errorHandler));
        employee.setCity(getCityFromCell(row.getCell(2)));
        employee.setOccupation(getOccupationFromCell(row.getCell(3)));
        employee.setOtherConditions(getStringCellValue(row.getCell(4)));
        employee.setBaseSalary(getNumericCellValue(row.getCell(5), "base salary", row.getRowNum(), errorHandler));
        employee.setProfessionalExperienceRate(getNumericCellValue(row.getCell(6), "professional experience rate", row.getRowNum(), errorHandler));
        employee.setFixedBonus(getNumericCellValue(row.getCell(7), "fixed bonus", row.getRowNum(), errorHandler));
    }

    private static String getEgnCellValue(Cell cell, int rowNum, ErrorHandler errorHandler) {
        if (cell == null) return "";

        try {
            return formatEgn(cell.getStringCellValue());
        } catch (IllegalStateException e) {
            try {
                return formatEgn(String.valueOf((long) cell.getNumericCellValue()));
            } catch (NumberFormatException | IllegalStateException ex) {
                errorHandler.addError("Invalid EGN format at row " + (rowNum + 1));
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
        if (cell == null) return 0;

        try {
            return cell.getNumericCellValue();
        } catch (IllegalStateException e) {
            errorHandler.addError("Invalid " + fieldName + " format at row " + (rowNum + 1));
            return 0;
        }
    }

    private static String formatEgn(String egn) {
        if (egn.length() == 9) {
            egn = "0" + egn;
        }
        return egn;
    }
}

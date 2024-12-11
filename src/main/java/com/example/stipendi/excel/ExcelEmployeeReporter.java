package com.example.stipendi.excel;

import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.model.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExcelEmployeeReporter {

    private final EmployeeDAO employeeDAO;

    public ExcelEmployeeReporter(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public String generateEmployeeReport(String filePath, int month, int year) {
        List<String> headers = Arrays.asList("ID", "EGN", "Full Name", "City", "Position", "Department", "Base Salary",
                "Professional Experience Bonus", "Achievement Bonus",
                "One Time Bonus", "Transport Bonus", "Fixed Bonus",
                "Total Overtime Week", "Total Overtime Weekend", "Total Working Days","Weekend", "Not worked hours", "$ for hour overtime", "Final Salary");

        List<List<Object>> data = getEmployeeData();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Stipendi OMS");

        // Create a cell style for bold headers
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // Create a cell style for currency format
        CellStyle currencyStyle = workbook.createCellStyle();
        DataFormat dataFormat = workbook.createDataFormat();
        currencyStyle.setDataFormat(dataFormat.getFormat("#,##0.00 \"лв.\""));

        // Write data rows
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            List<Object> rowData = data.get(i);

            for (int j = 0; j < rowData.size(); j++) {
                Cell cell = row.createCell(j);
                Object value = rowData.get(j);

                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());

                    // Apply currency style to specific columns
                    if (j == 6 || j == 7 || j == 8 || j == 9 || j == 10 || j == 11 || j == 17 || j == 18) {
                        cell.setCellStyle(currencyStyle);
                    }
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Save the workbook
        try (FileOutputStream fileOut = new FileOutputStream(filePath + "\\Stipendi OMS per mese " + month + "-" + year + ".xlsx" )) {
            workbook.write(fileOut);
            return "Докладът беше успешно записан.";
        } catch (FileNotFoundException e) {
            return "Файлът е отворен в друга програма. Моля, затворете файла и опитайте отново.";
        } catch (IOException e) {
            return "Възникна грешка при записване на отчета. Моля, опитайте отново.";
        }
    }

    private List<List<Object>> getEmployeeData() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        List<List<Object>> data = new ArrayList<>();

        for (Employee employee : employees) {
            List<Object> rowData = Arrays.asList(
                    employee.getId(),
                    employee.getEgn(),
                    employee.getFullName(),
                    employee.getCity().getCityName(),
                    employee.getOccupation().getPosition(),
                    employee.getOccupation().getDepartment(),
                    employee.getBaseSalary(),
                    employee.getProfessionalExperienceBonus(),
                    employee.getAchievementBonus(),
                    employee.getOneTimeBonus(),
                    employee.getTransportBonus(),
                    employee.getFixedBonus(),
                    employee.getTotalOvertimeWeek(),
                    employee.getTotalOvertimeWeekend(),
                    employee.getTotalWorkingDays(),
                    employee.getWeekend(),
                    employee.getRegularHours(),
                    employee.getPaymentForOvTimeHour(),
                    employee.getFinalSalary()
            );
            data.add(rowData);
        }

        return data;
    }
}
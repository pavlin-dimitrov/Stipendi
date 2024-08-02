package com.example.stipendi.util;

import com.example.stipendi.model.City;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.Occupation;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelFileUtil {


    public static Workbook getWorkbook(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = null;

        if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(fis);
        } else if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(fis);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    public static List<Employee> readEmployeesFromExcel(String filePath) {
        List<Employee> employeeList = new ArrayList<>();

        try (Workbook workbook = getWorkbook(filePath)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропускаме заглавния ред

                Employee employee = new Employee();
                employee.setFullName(row.getCell(0).getStringCellValue());
                employee.setEgn(row.getCell(1).getStringCellValue());

                City city = new City();
                city.setCityName(row.getCell(2).getStringCellValue());
                employee.setCity(city);

                Occupation occupation = new Occupation();
                occupation.setNkpd(row.getCell(3).getStringCellValue());
                employee.setOccupation(occupation);

                employee.setOtherConditions(row.getCell(4).getStringCellValue());
                employee.setBaseSalary(row.getCell(5).getNumericCellValue());
                employee.setProfessionalExperienceRate(row.getCell(6).getNumericCellValue());
                employee.setFixedBonus(row.getCell(7).getNumericCellValue());

                employeeList.add(employee);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList;
    }
}



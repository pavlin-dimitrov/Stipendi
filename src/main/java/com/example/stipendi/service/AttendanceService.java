package com.example.stipendi.service;

import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.excel.CheckInExcelReader;
import com.example.stipendi.model.Employee;
import com.example.stipendi.util.contract.ErrorHandler;

import java.util.List;

public class AttendanceService {

    private final EmployeeDAO employeeDAO;

    public AttendanceService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public void importAttendanceRecordsFromExcel(String attendanceFilePath, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        List<Employee> updatedEmployees = CheckInExcelReader.readCheckInRecordsFromExcel(attendanceFilePath, employees, errorHandler);

        for (Employee employee : updatedEmployees) {
            employeeDAO.updateEmployeeAttendance(employee);
        }
    }
}
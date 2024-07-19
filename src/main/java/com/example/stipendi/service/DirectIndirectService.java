package com.example.stipendi.service;

import com.example.stipendi.dao.DirectlyOccupiedDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.IndirectOccupiedDAO;
import com.example.stipendi.model.DirectlyOccupied;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.IndirectOccupied;

import java.util.List;

public class DirectIndirectService {

    private final EmployeeDAO employeeDAO;
    private final DirectlyOccupiedDAO directlyOccupiedDAO;
    private final IndirectOccupiedDAO indirectOccupiedDAO;

    public DirectIndirectService(EmployeeDAO employeeDAO, DirectlyOccupiedDAO directlyOccupiedDAO, IndirectOccupiedDAO indirectOccupiedDAO) {
        this.employeeDAO = employeeDAO;
        this.directlyOccupiedDAO = directlyOccupiedDAO;
        this.indirectOccupiedDAO = indirectOccupiedDAO;
    }

    public void updateDirectlyOccupied(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double totalDirectlyOccupiedHours = employees.stream()
                .filter(employee -> "P".equals(employee.getOtherConditions()) || "".equals(employee.getOtherConditions()))
                .mapToDouble(employee -> (employee.getTotalWorkingDays() * 8.0) + employee.getTotalOvertimeWeek() + employee.getTotalOvertimeWeekend())
                .sum();

        DirectlyOccupied directlyOccupied = new DirectlyOccupied();
        directlyOccupied.setYear(year);
        directlyOccupied.setMonth(month);
        directlyOccupied.setHours(totalDirectlyOccupiedHours);

        directlyOccupiedDAO.saveDirectlyOccupied(directlyOccupied);
    }

    public void updateIndirectOccupied(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double totalIndirectOccupiedHours = employees.stream()
                .filter(employee -> "I".equals(employee.getOtherConditions()) || "C".equals(employee.getOtherConditions()))
                .mapToDouble(employee -> (employee.getTotalWorkingDays() * 8.0) + employee.getTotalOvertimeWeek() + employee.getTotalOvertimeWeekend())
                .sum();

        IndirectOccupied indirectOccupied = new IndirectOccupied();
        indirectOccupied.setYear(year);
        indirectOccupied.setMonth(month);
        indirectOccupied.setHours(totalIndirectOccupiedHours);

        indirectOccupiedDAO.saveIndirectOccupied(indirectOccupied);
    }
}

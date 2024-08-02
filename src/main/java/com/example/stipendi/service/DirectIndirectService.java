package com.example.stipendi.service;

import com.example.stipendi.dao.DirectlyDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.IndirectDAO;
import com.example.stipendi.model.DirectlyOccupied;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.IndirectOccupied;

import java.util.List;

public class DirectIndirectService {

    private final EmployeeDAO employeeDAO;
    private final DirectlyDAO directlyDAO;
    private final IndirectDAO indirectDAO;

    public DirectIndirectService(EmployeeDAO employeeDAO, DirectlyDAO directlyDAO, IndirectDAO indirectDAO) {
        this.employeeDAO = employeeDAO;
        this.directlyDAO = directlyDAO;
        this.indirectDAO = indirectDAO;
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

        directlyDAO.saveDirectlyOccupied(directlyOccupied);
    }

    public void updateIndirectOccupied(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double totalIndirectOccupiedHours = employees.stream()
                .filter(employee -> "I".equals(employee.getOtherConditions().trim()) || "C".equals(employee.getOtherConditions().trim()))
                .mapToDouble(employee -> (employee.getTotalWorkingDays() * 8.0) + employee.getTotalOvertimeWeek() + employee.getTotalOvertimeWeekend())
                .sum();

        IndirectOccupied indirectOccupied = new IndirectOccupied();
        indirectOccupied.setYear(year);
        indirectOccupied.setMonth(month);
        indirectOccupied.setHours(totalIndirectOccupiedHours);

        indirectDAO.saveIndirectOccupied(indirectOccupied);
    }
}
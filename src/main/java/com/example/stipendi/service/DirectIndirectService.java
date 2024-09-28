package com.example.stipendi.service;

import com.example.stipendi.dao.DirectlyDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.IndirectDAO;
import com.example.stipendi.model.DirectlyOccupied;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.IndirectOccupied;

import java.util.List;
import java.util.Optional;

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
        double totalDirectlyOccupiedHours = calculateDirectlyOccupiedHours(employees);

        Optional<DirectlyOccupied> existingRecord = directlyDAO.findByMonthAndYear(month, year);

        if (existingRecord.isPresent()) {
            DirectlyOccupied directlyOccupied = existingRecord.get();
            directlyOccupied.setHours(totalDirectlyOccupiedHours);
            directlyDAO.updateDirectlyOccupied(directlyOccupied);
        } else {
            DirectlyOccupied newDirectlyOccupied = new DirectlyOccupied();
            newDirectlyOccupied.setYear(year);
            newDirectlyOccupied.setMonth(month);
            newDirectlyOccupied.setHours(totalDirectlyOccupiedHours);
            directlyDAO.saveDirectlyOccupied(newDirectlyOccupied);
        }
    }

    public void updateIndirectOccupied(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double totalIndirectOccupiedHours = calculateIndirectOccupiedHours(employees);

        Optional<IndirectOccupied> existingRecord = indirectDAO.findByMonthAndYear(month, year);

        if (existingRecord.isPresent()) {
            IndirectOccupied indirectOccupied = existingRecord.get();
            indirectOccupied.setHours(totalIndirectOccupiedHours);
            indirectDAO.updateIndirectOccupied(indirectOccupied);
        } else {
            IndirectOccupied newIndirectOccupied = new IndirectOccupied();
            newIndirectOccupied.setYear(year);
            newIndirectOccupied.setMonth(month);
            newIndirectOccupied.setHours(totalIndirectOccupiedHours);
            indirectDAO.saveIndirectOccupied(newIndirectOccupied);
        }
    }

    private double calculateDirectlyOccupiedHours(List<Employee> employees) {
        return employees.stream()
                .filter(employee -> "P".equals(employee.getOtherConditions()) || "".equals(employee.getOtherConditions()))
                .mapToDouble(this::calculateTotalHours)
                .sum();
    }

    private double calculateIndirectOccupiedHours(List<Employee> employees) {
        return employees.stream()
                .filter(employee -> "I".equals(employee.getOtherConditions().trim()) || "C".equals(employee.getOtherConditions().trim()))
                .mapToDouble(this::calculateTotalHours)
                .sum();
    }

    private double calculateTotalHours(Employee employee) {
        return (employee.getTotalWorkingDays() * 8.0) + employee.getTotalOvertimeWeek() + employee.getTotalOvertimeWeekend();
    }
}
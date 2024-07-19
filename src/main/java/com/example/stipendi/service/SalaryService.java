package com.example.stipendi.service;

import com.example.stipendi.dao.AppConfigVariableDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.model.Employee;
import com.example.stipendi.util.WorkdayCalculator;
import lombok.Data;

import java.util.List;

@Data
public class SalaryService {

    private final EmployeeDAO employeeDAO;
    private final WorkdayCalculator workdayCalculator;
    private final AppConfigVariableDAO appConfigVariableDAO;

    public SalaryService(EmployeeDAO employeeDAO, WorkdayCalculator workdayCalculator, AppConfigVariableDAO appConfigVariableDAO) {
        this.employeeDAO = employeeDAO;
        this.workdayCalculator = workdayCalculator;
        this.appConfigVariableDAO = appConfigVariableDAO;
    }

    public void updateEmployeeSalary(int month, int year){
        updateProfessionalExperienceBonus();
        updateOneTimeBonus(month, year);
        updateTransportBonus();
        updateFinalSalary();
    }

    private void updateProfessionalExperienceBonus() {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);
            employee.setProfessionalExperienceBonus(experienceBonus);
            employeeDAO.updateEmployee(employee);
        });
    }

    private void updateOneTimeBonus(int month, int year){
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);

            double hourPayment = ((employee.getBaseSalary() + employee.getFixedBonus() + experienceBonus) /
                    (workdayCalculator.getWorkdaysInMonth(month, year) * 8.0));

            double nightShift = 0;
            if (employee.getOtherConditions().equals("C") || employee.getOtherConditions().equals("")){
                nightShift = employee.getTotalOvertimeWeek() * appConfigVariableDAO.getAppConfigVariableByName("nightShift");
            }

            double oneTimeBonus = ((hourPayment * employee.getTotalOvertimeWeek()) *
                    appConfigVariableDAO.getAppConfigVariableByName("overtimeWeek")) +
                    ((hourPayment * employee.getTotalOvertimeWeekend()) *
                     appConfigVariableDAO.getAppConfigVariableByName("overtimeWeekend")) + nightShift;
            employee.setOneTimeBonus(oneTimeBonus);
            employeeDAO.updateEmployee(employee);
        });
    }

    private void updateTransportBonus(){
        List<Employee> employees = employeeDAO.getAllEmployees();

            employees.stream().forEach(employee -> {
                double transportBonus = 0;

                if (employee.getCity() != null && !employee.getCity().getCityName().equals("Враца")) {
                    double distance = employee.getCity().getDistance();
                    int workingDays = employee.getTotalWorkingDays();
                    double fuelRate = getAppConfigVariableDAO().getAppConfigVariableByName("fuel");

                    if ((distance * 2) - 30 > 0) {
                        transportBonus = ((distance * 2) - 30) * workingDays * fuelRate;
                    }
                }

                employee.setTransportBonus(transportBonus);
                employeeDAO.updateEmployee(employee);
            });
    }

    private void updateFinalSalary(){
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double finalSalary = employee.getBaseSalary() +
                    employee.getProfessionalExperienceBonus() +
                    employee.getAchievementBonus() +
                    employee.getOneTimeBonus() +
                    employee.getTransportBonus() +
                    employee.getFixedBonus();

            employee.setFinalSalary(finalSalary);
            employeeDAO.updateEmployee(employee);
        });
    }
}

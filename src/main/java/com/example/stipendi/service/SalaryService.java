package com.example.stipendi.service;

import com.example.stipendi.dao.AppConfigVariableDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.model.Employee;
import com.example.stipendi.util.WorkdayCalculator;
import com.example.stipendi.util.contract.ErrorHandler;
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

    public void updateEmployeeSalary(int month, int year, ErrorHandler errorHandler) {
        updateProfessionalExperienceBonus(errorHandler);
        updateTransportBonus(errorHandler);
        updateOneTimeBonus(month, year, errorHandler);
        updateFixedBonus(month, year, errorHandler);
        updateBaseSalary(month, year, errorHandler);
        updateAchievementBonus(errorHandler);
        updateFinalSalary(errorHandler);
    }

    private void updateAchievementBonus(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double achievementBonus = appConfigVariableDAO.getAppConfigVariableValueByName("achievementBonus");
        double partOfFixedBonus = appConfigVariableDAO.getAppConfigVariableValueByName("partOfAchievementBonus");

        employees.forEach(employee -> {
            double totalDaysOff = employee.getDaysOffDoo() + employee.getDaysOffEmpl();

            if (totalDaysOff > 0 && totalDaysOff < 2) {
                employee.setAchievementBonus(achievementBonus - partOfFixedBonus);
            } else if (totalDaysOff >= 2) {
                employee.setAchievementBonus(0);
            } else if (employee.getOtherConditions().toLowerCase().contains("x")) {
                employee.setAchievementBonus(0);
            } else {
                employee.setAchievementBonus(achievementBonus);
            }
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Achievement Bonuse calculated!");
    }

    private void updateOneTimeBonus(int month, int year, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);

            double hourPayment = ((employee.getBaseSalary() + employee.getFixedBonus() + experienceBonus) /
                    (workdayCalculator.getWorkdaysInMonth(month, year) * 8.0));

            double nightShift = 0;
            if (employee.getOtherConditions().toLowerCase().contains("c") || employee.getOtherConditions().equals("")) {
                nightShift = employee.getTotalOvertimeWeek() * appConfigVariableDAO.getAppConfigVariableValueByName("nightShift");
            }

            double oneTimeBonus = ((hourPayment * employee.getTotalOvertimeWeek()) *
                    appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeek")) +
                    ((hourPayment * employee.getTotalOvertimeWeekend()) *
                            appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeekend")) + nightShift;

            if (employee.getOtherConditions().toLowerCase().contains("i") && !employee.getOtherConditions().toLowerCase().contains("p")) {
                oneTimeBonus = 0;
            }

            employee.setOneTimeBonus(oneTimeBonus);
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("One Time Bonus calculated!");
    }

    private void updateFixedBonus(int month, int year, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        int workedDays = workdayCalculator.getWorkdaysInMonth(month, year);


        employees.forEach(employee -> {
            employee.setFixedBonus((employee.getFixedBonus() / workedDays) * employee.getTotalWorkingDays());
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Fixed Bonus calculated for " + workedDays + " working days");
    }

    private void updateProfessionalExperienceBonus(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);
            employee.setProfessionalExperienceBonus(experienceBonus);
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Professional Experience Bonus calculated!");
    }

    private void updateTransportBonus(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double transportBonus = 0;

            if (employee.getCity() != null && !employee.getCity().getCityName().equals("Враца")) {
                double distance = employee.getCity().getDistance();
                int workingDays = employee.getTotalWorkingDays();
                double fuelRate = getAppConfigVariableDAO().getAppConfigVariableValueByName("fuel");

                if ((distance * 2) - 30 > 0) {
                    transportBonus = ((distance * 2) - 30) * workingDays * fuelRate;
                }
            }

            employee.setTransportBonus(transportBonus);
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Transport Bonus Calculated!");
    }

    private void updateBaseSalary(int month, int year, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        int workedDays = workdayCalculator.getWorkdaysInMonth(month, year);

        employees.forEach(employee -> {
            employee.setBaseSalary((employee.getBaseSalary() / workedDays) * employee.getTotalWorkingDays());
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Base Salary Calculated!");
    }

    private void updateFinalSalary(ErrorHandler errorHandler) {
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
        errorHandler.addError("Final Salary Calculated!");
    }
}
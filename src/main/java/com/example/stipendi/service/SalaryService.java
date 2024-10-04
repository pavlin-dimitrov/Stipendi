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
        updateTransportBonus(errorHandler);
        updateBaseSalary(month, year, errorHandler);
        updateProfessionalExperienceBonus(errorHandler);
        updateOneTimeBonus(month, year, errorHandler);
        updateFixedBonus(month, year, errorHandler);
        updateAchievementBonus(errorHandler);
        updateFinalSalary(errorHandler);
    }

    private void updateAchievementBonus(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double achievementBonus = appConfigVariableDAO.getAppConfigVariableValueByName("achievementBonus");
        double partOfFixedBonus = appConfigVariableDAO.getAppConfigVariableValueByName("partOfAchievementBonus");
        double daysThatSetAchBonusToZero = appConfigVariableDAO.getAppConfigVariableValueByName("daysThatSetAchBonusToZero");

        employees.forEach(employee -> {
            double totalDaysOff = employee.getDaysOffDoo() + employee.getDaysOffEmpl();

            if (totalDaysOff > 0 && totalDaysOff < daysThatSetAchBonusToZero) {
                employee.setAchievementBonus(achievementBonus - partOfFixedBonus);
            } else if (totalDaysOff >= daysThatSetAchBonusToZero) {
                employee.setAchievementBonus(0);
            } else if (employee.getOtherConditions().toLowerCase().contains("x")) {
                employee.setAchievementBonus(0);
            } else if (employee.getOtherConditions().toLowerCase().contains("y")) {
                employee.setAchievementBonus(achievementBonus - partOfFixedBonus);
            } else {
                employee.setAchievementBonus(achievementBonus);
            }
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Achievement Bonuse calculated!");
    }

    private void updateOneTimeBonus(int month, int year, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double nightShiftRate = appConfigVariableDAO.getAppConfigVariableValueByName("nightShift");
        double overtimeWeekRate = appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeek");
        double overtimeWeekendRate = appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeekend");

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);

            double hourPayment = ((employee.getBaseSalary() + employee.getFixedBonus() + experienceBonus) /
                    (workdayCalculator.getWorkdaysInMonth(month, year) * 8.0));

            double nightShift = 0;
            if (employee.getOtherConditions().toLowerCase().contains("c") || employee.getOtherConditions().equals("")) {
                nightShift = employee.getTotalOvertimeWeek() * nightShiftRate;
            }

            double oneTimeBonus = ((hourPayment * employee.getTotalOvertimeWeek()) * overtimeWeekRate) +
                    ((hourPayment * employee.getTotalOvertimeWeekend()) * overtimeWeekendRate) + nightShift;

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
    int workingDays = workdayCalculator.getWorkdaysInMonth(month, year);

    for (Employee employee : employees) {
        int workedDays;
        if (employee.getTotalWorkingDays() > workingDays) {
            workedDays = workingDays;
        } else {
            workedDays = employee.getTotalWorkingDays();
        }

        // Ако workedDays < workingDays, изваждаме съботите
        if (workedDays < workingDays) {
            workedDays -= employee.getWeekend();
        }

        // Изчисляваме дневната ставка на бонуса и новия фиксиран бонус
        double dailyBonusRate = employee.getFixedBonus() / (double) workingDays;
        double newFixedBonus = dailyBonusRate * workedDays;

        employee.setFixedBonus(newFixedBonus);
        employeeDAO.updateEmployee(employee);
    }
    errorHandler.addError("Fixed Bonus calculated for " + workdayCalculator.getWorkdaysInMonth(month, year) + " working days.");
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
        int workingDays = workdayCalculator.getWorkdaysInMonth(month, year);

        for (Employee employee : employees) {
            int workedDays;
            if (employee.getTotalWorkingDays() > workingDays) {
                workedDays = workingDays;
            } else {
                workedDays = employee.getTotalWorkingDays();
            }

            // Ако workedDays < workingDays, изваждаме съботите
            if (workedDays < workingDays) {
                workedDays -= employee.getWeekend();
            }

            // Изчисляваме дневната ставка на заплатата и новата основна заплата
            double dailyRate = employee.getBaseSalary() / (double) workingDays;
            double newBaseSalary = dailyRate * workedDays;

            employee.setBaseSalary(newBaseSalary);
            employeeDAO.updateEmployee(employee);
        }

        errorHandler.addError("Base Salary Calculated!");
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

    private void updateFinalSalary(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            try {
                double finalSalary = calculateFinalSalary(employee);
                employee.setFinalSalary(finalSalary);
                employeeDAO.updateEmployee(employee);
            } catch (Exception e) {
                errorHandler.addError("Error calculating final salary for employee: " + employee.getId() + " - " + e.getMessage());
            }
        });

        errorHandler.addError("Final Salary Calculated for all employees!");
    }

    private double calculateFinalSalary(Employee employee) {
        if (employee.getTotalWorkingDays() == 0) {
            return 0;
        }

        return employee.getBaseSalary() +
                employee.getProfessionalExperienceBonus() +
                employee.getAchievementBonus() +
                employee.getOneTimeBonus() +
                employee.getTransportBonus() +
                employee.getFixedBonus();
    }

}
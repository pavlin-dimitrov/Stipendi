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
        updateOneTimeBonus(month, year, errorHandler);
        overtimeHourPayment(month, year, errorHandler);
        updateBaseSalary(month, year, errorHandler);
        updateFixedBonus(month, year, errorHandler);
        updateProfessionalExperienceBonus(errorHandler);
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
        errorHandler.addError("Изчислен бонус за постижения!");
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
            if (employee.getOtherConditions().toLowerCase().contains("c") || employee.getOtherConditions().toLowerCase().contains("d")) {
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
        errorHandler.addError("Изчислен еднократен бонус!");
    }

    private void overtimeHourPayment(int month, int year, ErrorHandler errorHandler){
        List<Employee> employees = employeeDAO.getAllEmployees();
        double overtimeWeekRate = appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeek");

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);

            double hourPayment = ((employee.getBaseSalary() + employee.getFixedBonus() + experienceBonus) /
                    (workdayCalculator.getWorkdaysInMonth(month, year) * 8.0));

            double hourPaymentOvertime = hourPayment * overtimeWeekRate;

            employee.setPaymentForOvTimeHour(hourPaymentOvertime);
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Изчислена делнична часова ставка за извънреден труд!");
    }

    private void updateFixedBonus(int month, int year, ErrorHandler errorHandler) {
    List<Employee> employees = employeeDAO.getAllEmployees();
    int workingDays = workdayCalculator.getWorkdaysInMonth(month, year);

    for (Employee employee : employees) {
        int workedDays;
            workedDays = employee.getTotalWorkingDays();
            workedDays -= employee.getWeekend();
        // Изчисляваме дневната ставка на бонуса и новия фиксиран бонус
        double dailyBonusRate = employee.getFixedBonus() / (double) workingDays;
        double newFixedBonus = dailyBonusRate * workedDays;

        employee.setFixedBonus(newFixedBonus);
        employeeDAO.updateEmployee(employee);
    }
    errorHandler.addError("Фиксиран бонус, изчислен за " + workdayCalculator.getWorkdaysInMonth(month, year) + " работни дни.");
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
        errorHandler.addError("Изчислен транспортен бонус!");
    }

    private void updateBaseSalary(int month, int year, ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        int workingDays = workdayCalculator.getWorkdaysInMonth(month, year);

        for (Employee employee : employees) {
            int workedDays;
                workedDays = employee.getTotalWorkingDays();
                workedDays -= employee.getWeekend();
            // Изчисляваме дневната ставка на заплатата и новата основна заплата
            double dailyRate = employee.getBaseSalary() / (double) workingDays;
            double newBaseSalary = dailyRate * workedDays;

            employee.setBaseSalary(newBaseSalary);
            employeeDAO.updateEmployee(employee);
        }

        errorHandler.addError("Изчислена основна заплата!");
    }

    private void updateProfessionalExperienceBonus(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary()) *
                    (employee.getProfessionalExperienceRate() / 100);
            employee.setProfessionalExperienceBonus(experienceBonus);
            employeeDAO.updateEmployee(employee);
        });
        errorHandler.addError("Изчислен бонус за професионален опит!");
    }

    private void updateFinalSalary(ErrorHandler errorHandler) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            try {
                double finalSalary = calculateFinalSalary(employee);
                employee.setFinalSalary(finalSalary);
                employeeDAO.updateEmployee(employee);
            } catch (Exception e) {
                errorHandler.addError("Грешка при изчисляване на крайната заплата за служител: " + employee.getId() + " - " + e.getMessage());
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
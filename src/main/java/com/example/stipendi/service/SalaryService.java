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

    public void updateEmployeeSalary(int month, int year) {
        updateProfessionalExperienceBonus();
        updateTransportBonus();
        updateFixedBonus(month, year);
        updateOneTimeBonus(month, year);
        updateAchievementBonus();
        updateFinalSalary();
    }

    private void updateAchievementBonus() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        double achievementBonus = appConfigVariableDAO.getAppConfigVariableValueByName("achievementBonus");
        double partOfFixedBonus = appConfigVariableDAO.getAppConfigVariableValueByName("partOfAchievementBonus");

        employees.forEach(employee -> {
            double totalDaysOff = employee.getDaysOffDoo() + employee.getDaysOffEmpl();

            if (totalDaysOff > 0 && totalDaysOff < 2) {
                employee.setAchievementBonus(achievementBonus - partOfFixedBonus);
                System.out.println(employee.getFullName() + " / " + achievementBonus + " - " + partOfFixedBonus);
            } else if (totalDaysOff >= 2) {
                employee.setAchievementBonus(0);
                System.out.println("Повече от 2 дни болничен");
            } else if (employee.getOtherConditions().toLowerCase().contains("x")) {
                employee.setAchievementBonus(0);
                System.out.println("Съдържа се Х в Други условия");
            } else {
                employee.setAchievementBonus(achievementBonus);
                System.out.println("100% Бонус");
            }
            employeeDAO.updateEmployee(employee);
        });
    }

    private void updateFixedBonus(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        int workedDays = workdayCalculator.getWorkdaysInMonth(month, year);


        employees.forEach(employee -> {
            employee.setFixedBonus((employee.getFixedBonus() / workedDays) * employee.getTotalWorkingDays());
            employeeDAO.updateEmployee(employee);
        });
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

    private void updateOneTimeBonus(int month, int year) {
        List<Employee> employees = employeeDAO.getAllEmployees();

        employees.stream().forEach(employee -> {
            double experienceBonus = (employee.getBaseSalary() + employee.getFixedBonus()) *
                    (employee.getProfessionalExperienceRate() / 100);

            double hourPayment = ((employee.getBaseSalary() + employee.getFixedBonus() + experienceBonus) /
                    (workdayCalculator.getWorkdaysInMonth(month, year) * 8.0));

            double nightShift = 0;
            if (employee.getOtherConditions().equals("C") || employee.getOtherConditions().equals("")) {
                nightShift = employee.getTotalOvertimeWeek() * appConfigVariableDAO.getAppConfigVariableValueByName("nightShift");
            }

            double oneTimeBonus = ((hourPayment * employee.getTotalOvertimeWeek()) *
                    appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeek")) +
                    ((hourPayment * employee.getTotalOvertimeWeekend()) *
                            appConfigVariableDAO.getAppConfigVariableValueByName("overtimeWeekend")) + nightShift;
            employee.setOneTimeBonus(oneTimeBonus);
            employeeDAO.updateEmployee(employee);
        });
    }

    private void updateTransportBonus() {
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
    }

    private void updateFinalSalary() {
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
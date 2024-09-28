package com.example.stipendi.dao;

import com.example.stipendi.model.Employee;
import com.example.stipendi.model.Occupation;
import com.example.stipendi.model.City;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class EmployeeDAO {

    private final CityDAO cityDAO;
    private final OccupationDAO occupationDAO;

    public EmployeeDAO() {
        this.cityDAO = new CityDAO();
        this.occupationDAO = new OccupationDAO();
    }

    public void saveEmployee(Employee employee) {
        String query = "INSERT INTO employees (egn, full_name, city_id, occupation_id, base_salary, " +
                "professional_experience_rate, professional_experience_bonus, achievement_bonus, one_time_bonus, " +
                "transport_bonus, fixed_bonus, other_conditions, days_off_doo, days_off_empl, total_overtime_week, total_overtime_weekend, " +
                "total_working_days, weekend, final_salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employee.getEgn());
            preparedStatement.setString(2, employee.getFullName());
            preparedStatement.setInt(3, employee.getCity().getId());
            preparedStatement.setInt(4, employee.getOccupation().getId());
            preparedStatement.setDouble(5, employee.getBaseSalary());
            preparedStatement.setDouble(6, employee.getProfessionalExperienceRate());
            preparedStatement.setDouble(7, employee.getProfessionalExperienceBonus());
            preparedStatement.setDouble(8, employee.getAchievementBonus());
            preparedStatement.setDouble(9, employee.getOneTimeBonus());
            preparedStatement.setDouble(10, employee.getTransportBonus());
            preparedStatement.setDouble(11, employee.getFixedBonus());
            preparedStatement.setString(12, employee.getOtherConditions());
            preparedStatement.setDouble(13, employee.getDaysOffDoo());
            preparedStatement.setDouble(14, employee.getDaysOffEmpl());
            preparedStatement.setInt(15, employee.getTotalOvertimeWeek());
            preparedStatement.setInt(16, employee.getTotalOvertimeWeekend());
            preparedStatement.setInt(17, employee.getTotalWorkingDays());
            preparedStatement.setInt(18, employee.getWeekend());
            preparedStatement.setDouble(19, employee.getFinalSalary());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> getAllEmployees() {
        String query = "SELECT * FROM employees";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getInt("id"));
                employee.setEgn(resultSet.getString("egn"));
                employee.setFullName(resultSet.getString("full_name"));

                City city = cityDAO.getCityById(resultSet.getInt("city_id"));
                employee.setCity(city);

                Occupation occupation = occupationDAO.getOccupationById(resultSet.getInt("occupation_id"));
                employee.setOccupation(occupation);

                employee.setBaseSalary(resultSet.getDouble("base_salary"));
                employee.setProfessionalExperienceRate(resultSet.getDouble("professional_experience_rate"));
                employee.setProfessionalExperienceBonus(resultSet.getDouble("professional_experience_bonus"));
                employee.setAchievementBonus(resultSet.getDouble("achievement_bonus"));
                employee.setOneTimeBonus(resultSet.getDouble("one_time_bonus"));
                employee.setTransportBonus(resultSet.getDouble("transport_bonus"));
                employee.setFixedBonus(resultSet.getDouble("fixed_bonus"));
                employee.setOtherConditions(resultSet.getString("other_conditions"));
                employee.setDaysOffDoo(resultSet.getDouble("days_off_doo"));
                employee.setDaysOffEmpl(resultSet.getDouble("days_off_empl"));
                employee.setTotalOvertimeWeek(resultSet.getInt("total_overtime_week"));
                employee.setTotalOvertimeWeekend(resultSet.getInt("total_overtime_weekend"));
                employee.setTotalWorkingDays(resultSet.getInt("total_working_days"));
                employee.setWeekend(resultSet.getInt("weekend"));
                employee.setFinalSalary(resultSet.getDouble("final_salary"));

                employees.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public void updateEmployee(Employee employee) {
        String query = "UPDATE employees SET egn = ?, full_name = ?, city_id = ?, occupation_id = ?, base_salary = ?, " +
                "professional_experience_rate = ?, professional_experience_bonus = ?, achievement_bonus = ?, " +
                "one_time_bonus = ?, transport_bonus = ?, fixed_bonus = ?, other_conditions = ?, " +
                "days_off_doo = ?, days_off_empl = ?, total_overtime_week = ?, total_overtime_weekend = ?, " +
                "total_working_days = ?, weekend = ?, final_salary = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, employee.getEgn());
            preparedStatement.setString(2, employee.getFullName());
            preparedStatement.setInt(3, employee.getCity().getId());
            preparedStatement.setInt(4, employee.getOccupation().getId());
            preparedStatement.setDouble(5, employee.getBaseSalary());
            preparedStatement.setDouble(6, employee.getProfessionalExperienceRate());
            preparedStatement.setDouble(7, employee.getProfessionalExperienceBonus());
            preparedStatement.setDouble(8, employee.getAchievementBonus());
            preparedStatement.setDouble(9, employee.getOneTimeBonus());
            preparedStatement.setDouble(10, employee.getTransportBonus());
            preparedStatement.setDouble(11, employee.getFixedBonus());
            preparedStatement.setString(12, employee.getOtherConditions());
            preparedStatement.setDouble(13, employee.getDaysOffDoo());
            preparedStatement.setDouble(14, employee.getDaysOffEmpl());
            preparedStatement.setInt(15, employee.getTotalOvertimeWeek());
            preparedStatement.setInt(16, employee.getTotalOvertimeWeekend());
            preparedStatement.setInt(17, employee.getTotalWorkingDays());
            preparedStatement.setInt(18, employee.getWeekend());
            preparedStatement.setDouble(19, employee.getFinalSalary());
            preparedStatement.setInt(20, employee.getId());

            int rowsUpdated = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEmployeeAttendance(Employee employee) {
        String query = "UPDATE employees SET total_overtime_week = ?, total_overtime_weekend = ?, total_working_days = ?, weekend = ? WHERE egn = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, employee.getTotalOvertimeWeek());
            preparedStatement.setInt(2, employee.getTotalOvertimeWeekend());
            preparedStatement.setInt(3, employee.getTotalWorkingDays());
            preparedStatement.setInt(4, employee.getWeekend());
            preparedStatement.setString(5, employee.getEgn());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearEmployeesTable() {
        String deleteQuery = "DELETE FROM employees";
        String resetAutoIncrementQuery = "ALTER TABLE employees AUTO_INCREMENT = 1";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
             PreparedStatement resetStatement = connection.prepareStatement(resetAutoIncrementQuery)) {

            // Изтриване на всички записи от таблицата
            deleteStatement.executeUpdate();

            // Ресетиране на автоинкрементния брояч
            resetStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
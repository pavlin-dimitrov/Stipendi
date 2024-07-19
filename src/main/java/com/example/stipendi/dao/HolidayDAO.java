package com.example.stipendi.dao;

import com.example.stipendi.model.Holiday;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HolidayDAO {

    public void saveHoliday(Holiday holiday) {
        String query = "INSERT INTO holidays (month_num, holiday_num) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, holiday.getMonthNum());
            preparedStatement.setInt(2, holiday.getHolidayNum());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        holiday.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Holiday> getHolidaysByMonth(int monthNum) {
        String query = "SELECT * FROM holidays WHERE month_num = ?";
        List<Holiday> holidays = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, monthNum);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Holiday holiday = new Holiday();
                holiday.setId(resultSet.getInt("id"));
                holiday.setMonthNum(resultSet.getInt("month_num"));
                holiday.setHolidayNum(resultSet.getInt("holiday_num"));
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holidays;
    }

    public int getHolidayNumByMonth(int monthNum) {
        String query = "SELECT holiday_num FROM holidays WHERE month_num = ?";
        int holidayNum = -1; // default value if no result found

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, monthNum);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                holidayNum = resultSet.getInt("holiday_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holidayNum;
    }

    public List<Holiday> getAllHolidays() {
        String query = "SELECT * FROM holidays";
        List<Holiday> holidays = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Holiday holiday = new Holiday();
                holiday.setId(resultSet.getInt("id"));
                holiday.setMonthNum(resultSet.getInt("month_num"));
                holiday.setHolidayNum(resultSet.getInt("holiday_num"));
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holidays;
    }

    public void updateHoliday(Holiday holiday) {
        String query = "UPDATE holidays SET month_num = ?, holiday_num = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, holiday.getMonthNum());
            preparedStatement.setInt(2, holiday.getHolidayNum());
            preparedStatement.setInt(3, holiday.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteHoliday(int id) {
        String query = "DELETE FROM holidays WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

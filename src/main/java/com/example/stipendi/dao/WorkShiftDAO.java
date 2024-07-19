package com.example.stipendi.dao;

import com.example.stipendi.model.WorkShift;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkShiftDAO {

    public void saveWorkShift(WorkShift workShift) {
        String query = "INSERT INTO work_shifts (type, startTime, endTime) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, workShift.getType());
            preparedStatement.setTime(2, Time.valueOf(workShift.getStartTime()));
            preparedStatement.setTime(3, Time.valueOf(workShift.getEndTime()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public WorkShift getWorkShiftById(int id) {
        String query = "SELECT * FROM work_shifts WHERE id = ?";
        WorkShift workShift = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                workShift = new WorkShift();
                workShift.setId(resultSet.getInt("id"));
                workShift.setType(resultSet.getString("type"));
                workShift.setStartTime(resultSet.getTime("startTime").toLocalTime());
                workShift.setEndTime(resultSet.getTime("endTime").toLocalTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workShift;
    }

    public List<WorkShift> getAllWorkShifts() {
        String query = "SELECT * FROM work_shifts";
        List<WorkShift> workShifts = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                WorkShift workShift = new WorkShift();
                workShift.setId(resultSet.getInt("id"));
                workShift.setType(resultSet.getString("type"));
                workShift.setStartTime(resultSet.getTime("startTime").toLocalTime());
                workShift.setEndTime(resultSet.getTime("endTime").toLocalTime());

                workShifts.add(workShift);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workShifts;
    }

    public void updateWorkShift(WorkShift workShift) {
        String query = "UPDATE work_shifts SET type = ?, startTime = ?, endTime = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, workShift.getType());
            preparedStatement.setTime(2, Time.valueOf(workShift.getStartTime()));
            preparedStatement.setTime(3, Time.valueOf(workShift.getEndTime()));
            preparedStatement.setInt(4, workShift.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteWorkShift(int id) {
        String query = "DELETE FROM work_shifts WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


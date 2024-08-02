package com.example.stipendi.dao;

import com.example.stipendi.model.DirectlyOccupied;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectlyDAO {

    public List<DirectlyOccupied> getByYear(int year) {
        String query = "SELECT * FROM directly_occupied WHERE year = ?";
        List<DirectlyOccupied> results = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, year);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DirectlyOccupied record = new DirectlyOccupied(
                        resultSet.getInt("id"),
                        resultSet.getInt("year"),
                        resultSet.getInt("month"),
                        resultSet.getDouble("hours")
                );
                results.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public List<Integer> getAllYears() {
        String query = "SELECT DISTINCT year FROM directly_occupied ORDER BY year";
        List<Integer> years = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                years.add(resultSet.getInt("year"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return years;
    }

    public void saveDirectlyOccupied(DirectlyOccupied directlyOccupied) {
        String query = "INSERT INTO directly_occupied (year, month, hours) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, directlyOccupied.getYear());
            preparedStatement.setInt(2, directlyOccupied.getMonth());
            preparedStatement.setDouble(3, directlyOccupied.getHours());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DirectlyOccupied getDirectlyOccupiedById(int id) {
        String query = "SELECT * FROM directly_occupied WHERE id = ?";
        DirectlyOccupied directlyOccupied = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                directlyOccupied = new DirectlyOccupied();
                directlyOccupied.setId(resultSet.getInt("id"));
                directlyOccupied.setYear(resultSet.getInt("year"));
                directlyOccupied.setMonth(resultSet.getInt("month"));
                directlyOccupied.setHours(resultSet.getDouble("hours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return directlyOccupied;
    }

    public List<DirectlyOccupied> getAllDirectlyOccupied() {
        String query = "SELECT * FROM directly_occupied";
        List<DirectlyOccupied> directlyOccupiedList = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                DirectlyOccupied directlyOccupied = new DirectlyOccupied();
                directlyOccupied.setId(resultSet.getInt("id"));
                directlyOccupied.setYear(resultSet.getInt("year"));
                directlyOccupied.setMonth(resultSet.getInt("month"));
                directlyOccupied.setHours(resultSet.getDouble("hours"));

                directlyOccupiedList.add(directlyOccupied);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return directlyOccupiedList;
    }

    public void updateDirectlyOccupied(DirectlyOccupied directlyOccupied) {
        String query = "UPDATE directly_occupied SET year = ?, month = ?, hours = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, directlyOccupied.getYear());
            preparedStatement.setInt(2, directlyOccupied.getMonth());
            preparedStatement.setDouble(3, directlyOccupied.getHours());
            preparedStatement.setInt(4, directlyOccupied.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDirectlyOccupied(int id) {
        String query = "DELETE FROM directly_occupied WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
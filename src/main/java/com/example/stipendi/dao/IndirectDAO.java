package com.example.stipendi.dao;

import com.example.stipendi.model.IndirectOccupied;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IndirectDAO {

    public List<IndirectOccupied> getByYear(int year) {
        String query = "SELECT * FROM indirect_occupied WHERE year = ?";
        List<IndirectOccupied> results = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, year);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                IndirectOccupied record = new IndirectOccupied(
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
        String query = "SELECT DISTINCT year FROM indirect_occupied ORDER BY year";
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

    public void saveIndirectOccupied(IndirectOccupied indirectOccupied) {
        String query = "INSERT INTO indirect_occupied (year, month, hours) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, indirectOccupied.getYear());
            preparedStatement.setInt(2, indirectOccupied.getMonth());
            preparedStatement.setDouble(3, indirectOccupied.getHours());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IndirectOccupied getIndirectOccupiedById(int id) {
        String query = "SELECT * FROM indirect_occupied WHERE id = ?";
        IndirectOccupied indirectOccupied = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                indirectOccupied = new IndirectOccupied();
                indirectOccupied.setId(resultSet.getInt("id"));
                indirectOccupied.setYear(resultSet.getInt("year"));
                indirectOccupied.setMonth(resultSet.getInt("month"));
                indirectOccupied.setHours(resultSet.getDouble("hours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indirectOccupied;
    }

    public List<IndirectOccupied> getAllIndirectOccupied() {
        String query = "SELECT * FROM indirect_occupied";
        List<IndirectOccupied> indirectOccupiedList = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                IndirectOccupied indirectOccupied = new IndirectOccupied();
                indirectOccupied.setId(resultSet.getInt("id"));
                indirectOccupied.setYear(resultSet.getInt("year"));
                indirectOccupied.setMonth(resultSet.getInt("month"));
                indirectOccupied.setHours(resultSet.getDouble("hours"));

                indirectOccupiedList.add(indirectOccupied);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indirectOccupiedList;
    }

    public void updateIndirectOccupied(IndirectOccupied indirectOccupied) {
        String query = "UPDATE indirect_occupied SET year = ?, month = ?, hours = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, indirectOccupied.getYear());
            preparedStatement.setInt(2, indirectOccupied.getMonth());
            preparedStatement.setDouble(3, indirectOccupied.getHours());
            preparedStatement.setInt(4, indirectOccupied.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteIndirectOccupied(int id) {
        String query = "DELETE FROM indirect_occupied WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<IndirectOccupied> findByMonthAndYear(int month, int year) {
        String query = "SELECT * FROM indirect_occupied WHERE year = ? AND month = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    IndirectOccupied indirectOccupied = new IndirectOccupied();
                    indirectOccupied.setId(rs.getInt("id"));
                    indirectOccupied.setYear(rs.getInt("year"));
                    indirectOccupied.setMonth(rs.getInt("month"));
                    indirectOccupied.setHours(rs.getDouble("hours"));
                    return Optional.of(indirectOccupied);
                }
            }
        } catch (SQLException e) {
            // Можете да изберете да логвате грешката или да я предадете нагоре
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
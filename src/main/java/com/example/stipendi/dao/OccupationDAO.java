package com.example.stipendi.dao;

import com.example.stipendi.model.Occupation;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OccupationDAO {

    public void saveOccupation(Occupation occupation) {
        String query = "INSERT INTO occupations (nkpd, department, position) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, occupation.getNkpd());
            preparedStatement.setString(2, occupation.getDepartment() != null ? occupation.getDepartment() : "");
            preparedStatement.setString(3, occupation.getPosition() != null ? occupation.getPosition() : "");

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Occupation getOccupationById(int id) {
        String query = "SELECT * FROM occupations WHERE id = ?";
        Occupation occupation = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                occupation = new Occupation();
                occupation.setId(resultSet.getInt("id"));
                occupation.setDepartment(resultSet.getString("department"));
                occupation.setPosition(resultSet.getString("position"));
                occupation.setNkpd(resultSet.getString("nkpd"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return occupation;
    }

    public List<Occupation> getAllOccupations() {
        String query = "SELECT * FROM occupations";
        List<Occupation> occupations = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Occupation occupation = new Occupation();
                occupation.setId(resultSet.getInt("id"));
                occupation.setDepartment(resultSet.getString("department"));
                occupation.setPosition(resultSet.getString("position"));
                occupation.setNkpd(resultSet.getString("nkpd"));

                occupations.add(occupation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return occupations;
    }

    public void updateOccupation(Occupation occupation) {
        String query = "UPDATE occupations SET department = ?, position = ?, nkpd = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, occupation.getDepartment());
            preparedStatement.setString(2, occupation.getPosition());
            preparedStatement.setString(3, occupation.getNkpd());
            preparedStatement.setInt(4, occupation.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOccupation(int id) {
        String query = "DELETE FROM occupations WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Occupation getOccupationByNKPD(String nkpd) {
        String query = "SELECT * FROM occupations WHERE nkpd = ?";
        Occupation occupation = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nkpd);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                occupation = new Occupation();
                occupation.setId(resultSet.getInt("id"));
                occupation.setDepartment(resultSet.getString("department"));
                occupation.setPosition(resultSet.getString("position"));
                occupation.setNkpd(resultSet.getString("nkpd"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return occupation;
    }

    public int getEmployeeCountByOccupation(int occupationId) throws SQLException {
        String query = "SELECT COUNT(*) FROM employees WHERE occupation_id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, occupationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }
}
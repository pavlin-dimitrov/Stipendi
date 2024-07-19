package com.example.stipendi.dao;

import com.example.stipendi.model.AppConfigVariable;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppConfigVariableDAO {

    public void saveAppConfigVariable(AppConfigVariable appConfigVariable) {
        String query = "INSERT INTO app_config_variables (variableName, variableValue) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, appConfigVariable.getVariableName());
            preparedStatement.setDouble(2, appConfigVariable.getVariableValue());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AppConfigVariable getAppConfigVariableById(int id) {
        String query = "SELECT * FROM app_config_variables WHERE id = ?";
        AppConfigVariable appConfigVariable = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                appConfigVariable = new AppConfigVariable(
                        resultSet.getInt("id"),
                        resultSet.getString("variable_name"),
                        resultSet.getDouble("variable_value")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appConfigVariable;
    }

    public double getAppConfigVariableByName(String variableName) {
        String query = "SELECT * FROM app_config_variables WHERE variable_name = ?";
        AppConfigVariable appConfigVariable = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, variableName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                appConfigVariable = new AppConfigVariable(
                        resultSet.getInt("id"),
                        resultSet.getString("variable_name"),
                        resultSet.getDouble("variable_value")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appConfigVariable.getVariableValue();
    }

    public List<AppConfigVariable> getAllAppConfigVariables() {
        String query = "SELECT * FROM app_config_variables";
        List<AppConfigVariable> appConfigVariables = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                AppConfigVariable appConfigVariable = new AppConfigVariable(
                        resultSet.getInt("id"),
                        resultSet.getString("variable_name"),
                        resultSet.getDouble("variable_value")
                );

                appConfigVariables.add(appConfigVariable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appConfigVariables;
    }

    public void updateAppConfigVariable(AppConfigVariable appConfigVariable) {
        String query = "UPDATE app_config_variables SET variable_name = ?, variable_value = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, appConfigVariable.getVariableName());
            preparedStatement.setDouble(2, appConfigVariable.getVariableValue());
            preparedStatement.setInt(3, appConfigVariable.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAppConfigVariable(int id) {
        String query = "DELETE FROM app_config_variables WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

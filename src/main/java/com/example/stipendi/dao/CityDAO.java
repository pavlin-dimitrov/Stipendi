package com.example.stipendi.dao;

import com.example.stipendi.model.City;
import com.example.stipendi.util.DatabaseHandler;
import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CityDAO {

    public boolean saveCity(City city) {
        String query = "INSERT INTO cities (city_name, distance) VALUES (?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Проверка дали градът вече съществува
            if (cityExists(city.getCityName())) {
                System.out.println("Град с това име съществува: " + city.getCityName());
                return false;
            }

            preparedStatement.setString(1, city.getCityName());
            preparedStatement.setDouble(2, city.getDistance());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public City getCityById(int id) {
        String query = "SELECT * FROM cities WHERE id = ?";
        City city = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                city = new City();
                city.setId(resultSet.getInt("id"));
                city.setCityName(resultSet.getString("city_name"));
                city.setDistance(resultSet.getDouble("distance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return city;
    }

    public List<City> getAllCities() {
        String query = "SELECT * FROM cities";
        List<City> cities = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                City city = new City();
                city.setId(resultSet.getInt("id"));
                city.setCityName(resultSet.getString("city_name"));
                city.setDistance(resultSet.getDouble("distance"));

                cities.add(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cities;
    }

    public void updateCity(City city) {
        String query = "UPDATE cities SET city_name = ?, distance = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Проверка дали градът вече съществува
            if (cityExists(city.getCityName()) && !isSameCity(city)) {
                System.out.println("City already exists: " + city.getCityName());
                return;
            }

            preparedStatement.setString(1, city.getCityName());
            preparedStatement.setDouble(2, city.getDistance());
            preparedStatement.setInt(3, city.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCity(int id) {
        String query = "DELETE FROM cities WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getEmployeeCountByCity(int cityId) throws SQLException {
        String query = "SELECT COUNT(*) FROM employees WHERE city_id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }


    private boolean cityExists(String city_name) {
        String query = "SELECT COUNT(*) FROM cities WHERE city_name = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, city_name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isSameCity(City city) {
        String query = "SELECT COUNT(*) FROM cities WHERE city_name = ? AND id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, city.getCityName());
            preparedStatement.setInt(2, city.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public City getCityByName(String city_name) {
        String query = "SELECT * FROM cities WHERE city_name = ?";
        City city = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, city_name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                city = new City();
                city.setId(resultSet.getInt("id"));
                city.setCityName(resultSet.getString("city_name"));
                city.setDistance(resultSet.getDouble("distance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return city;
    }
}
package com.example.stipendi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    //    private static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
    private static final String URL = "jdbc:mariadb://localhost:3309/payroll_db";

    private static final String DB_NAME = "payroll_db";
    private static final String USER = "root"; // Заменете с вашето потребителско име за MySQL
    private static final String PASSWORD = "root"; // Заменете с вашата парола за MySQL

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Създаване на базата данни, ако не съществува
    public static void createDatabaseIfNotExists() {
        String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createDatabaseSQL);
            System.out.println("Database created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    // Създаване на всички таблици, ако не съществуват
    public static void createTablesIfNotExists() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String createCitiesTable = """
                CREATE TABLE IF NOT EXISTS cities (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    city_name VARCHAR(100) NOT NULL,
                    distance DOUBLE NOT NULL,
                    UNIQUE (city_name)
                );
            """;

            String createAppConfigVariablesTable = """
                CREATE TABLE IF NOT EXISTS app_config_variables (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    variable_name VARCHAR(50) NOT NULL UNIQUE,
                    variable_value DOUBLE NOT NULL
                );
            """;

            String createOccupationsTable = """
                CREATE TABLE IF NOT EXISTS occupations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    department VARCHAR(150),
                    position VARCHAR(150),
                    nkpd VARCHAR(20) NOT NULL UNIQUE
                );
            """;

            String createEmployeesTable = """
                        CREATE TABLE IF NOT EXISTS employees (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            egn VARCHAR(10) NOT NULL UNIQUE,
                            full_name VARCHAR(100) NOT NULL,
                            city_id INT,
                            occupation_id INT,
                            base_salary DOUBLE,
                            professional_experience_rate DOUBLE,
                            professional_experience_bonus DOUBLE,
                            achievement_bonus DOUBLE,
                            one_time_bonus DOUBLE,
                            transport_bonus DOUBLE,
                            fixed_bonus DOUBLE,
                            other_conditions VARCHAR(255),
                            days_off_doo DOUBLE,
                            days_off_empl DOUBLE,
                            total_overtime_week int,
                            total_overtime_weekend int,
                            total_working_days int,
                            weekend int,
                            regular_hours DOUBLE,
                            payment_for_ov_time_hour DOUBLE,
                            final_salary DOUBLE,
                            FOREIGN KEY (city_id) REFERENCES cities(id),
                            FOREIGN KEY (occupation_id) REFERENCES occupations(id)
                        );
                    """;

            String createWorkShiftsTable = """
                CREATE TABLE IF NOT EXISTS work_shifts (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    type VARCHAR(50),
                    start_time TIME,
                    end_time TIME
                );
            """;

            String createAttendanceRecordsTable = """
                CREATE TABLE IF NOT EXISTS attendance_records (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    egn VARCHAR(10) NOT NULL,
                    entry_time DATETIME,
                    exit_time DATETIME,
                    work_shift_id INT,
                    regular_hours DOUBLE,
                    overtime_hours DOUBLE,
                    total_hours DOUBLE,
                    FOREIGN KEY (egn) REFERENCES employees(egn),
                    FOREIGN KEY (work_shift_id) REFERENCES work_shifts(id)
                );
            """;

            String createHolidaysTable = """
                CREATE TABLE IF NOT EXISTS holidays (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    month_num INT NOT NULL,
                    holiday_num INT NOT NULL
                );
            """;

            String createDirectlyOccupiedTable = """
                CREATE TABLE IF NOT EXISTS directly_occupied (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    year INT,
                    month INT,
                    hours DOUBLE
                );
            """;

            String createIndirectOccupiedTable = """
                CREATE TABLE IF NOT EXISTS indirect_occupied (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    year INT,
                    month INT,
                    hours DOUBLE
                );
            """;

            // Изпълняваме всички заявки
            stmt.executeUpdate(createCitiesTable);
            stmt.executeUpdate(createAppConfigVariablesTable);
            stmt.executeUpdate(createOccupationsTable);
            stmt.executeUpdate(createEmployeesTable);
            stmt.executeUpdate(createWorkShiftsTable);
            stmt.executeUpdate(createAttendanceRecordsTable);
            stmt.executeUpdate(createHolidaysTable);
            stmt.executeUpdate(createDirectlyOccupiedTable);
            stmt.executeUpdate(createIndirectOccupiedTable);

            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void testConnection() {
        try (Connection conn = connect()) {
            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Метод за настройка на базата данни при стартиране
    public static void setupDatabase() {
        createDatabaseIfNotExists();
        createTablesIfNotExists();
    }


    public static void main(String[] args) {
        setupDatabase();
    }
}
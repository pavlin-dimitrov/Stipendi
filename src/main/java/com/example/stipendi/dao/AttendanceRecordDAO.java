package com.example.stipendi.dao;

import com.example.stipendi.model.AttendanceRecord;
import com.example.stipendi.model.WorkShift;
import com.example.stipendi.util.DatabaseHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceRecordDAO {

    private WorkShiftDAO workShiftDAO;

    public AttendanceRecordDAO() {
        this.workShiftDAO = new WorkShiftDAO();
    }

    public void saveAttendanceRecord(AttendanceRecord attendanceRecord) {
        String query = "INSERT INTO attendance_records (egn, entryTime, exitTime, workShift_id, regularHours, overtimeHours, totalHours) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, attendanceRecord.getEgn());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(attendanceRecord.getEntryTime()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(attendanceRecord.getExitTime()));
            preparedStatement.setInt(4, attendanceRecord.getWorkShift().getId());
            preparedStatement.setDouble(5, attendanceRecord.getRegularHours());
            preparedStatement.setDouble(6, attendanceRecord.getOvertimeHours());
            preparedStatement.setDouble(7, attendanceRecord.getTotalHours());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AttendanceRecord getAttendanceRecordById(int id) {
        String query = "SELECT * FROM attendance_records WHERE id = ?";
        AttendanceRecord attendanceRecord = null;

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                attendanceRecord = new AttendanceRecord();
                attendanceRecord.setId(resultSet.getInt("id"));
                attendanceRecord.setEgn(resultSet.getString("egn"));
                attendanceRecord.setEntryTime(resultSet.getTimestamp("entryTime").toLocalDateTime());
                attendanceRecord.setExitTime(resultSet.getTimestamp("exitTime").toLocalDateTime());
                WorkShift workShift = workShiftDAO.getWorkShiftById(resultSet.getInt("workShift_id"));
                attendanceRecord.setWorkShift(workShift);
                attendanceRecord.setRegularHours(resultSet.getDouble("regularHours"));
                attendanceRecord.setOvertimeHours(resultSet.getDouble("overtimeHours"));
                attendanceRecord.setTotalHours(resultSet.getDouble("totalHours"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceRecord;
    }

    public List<AttendanceRecord> getAllAttendanceRecords() {
        String query = "SELECT * FROM attendance_records";
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                AttendanceRecord attendanceRecord = new AttendanceRecord();
                attendanceRecord.setId(resultSet.getInt("id"));
                attendanceRecord.setEgn(resultSet.getString("egn"));
                attendanceRecord.setEntryTime(resultSet.getTimestamp("entryTime").toLocalDateTime());
                attendanceRecord.setExitTime(resultSet.getTimestamp("exitTime").toLocalDateTime());
                WorkShift workShift = workShiftDAO.getWorkShiftById(resultSet.getInt("workShift_id"));
                attendanceRecord.setWorkShift(workShift);
                attendanceRecord.setRegularHours(resultSet.getDouble("regularHours"));
                attendanceRecord.setOvertimeHours(resultSet.getDouble("overtimeHours"));
                attendanceRecord.setTotalHours(resultSet.getDouble("totalHours"));

                attendanceRecords.add(attendanceRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attendanceRecords;
    }

    public void updateAttendanceRecord(AttendanceRecord attendanceRecord) {
        String query = "UPDATE attendance_records SET egn = ?, entryTime = ?, exitTime = ?, workShift_id = ?, regularHours = ?, overtimeHours = ?, totalHours = ? WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, attendanceRecord.getEgn());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(attendanceRecord.getEntryTime()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(attendanceRecord.getExitTime()));
            preparedStatement.setInt(4, attendanceRecord.getWorkShift().getId());
            preparedStatement.setDouble(5, attendanceRecord.getRegularHours());
            preparedStatement.setDouble(6, attendanceRecord.getOvertimeHours());
            preparedStatement.setDouble(7, attendanceRecord.getTotalHours());
            preparedStatement.setInt(8, attendanceRecord.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAttendanceRecord(int id) {
        String query = "DELETE FROM attendance_records WHERE id = ?";

        try (Connection connection = DatabaseHandler.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

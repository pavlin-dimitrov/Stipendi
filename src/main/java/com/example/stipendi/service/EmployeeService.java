package com.example.stipendi.service;

import com.example.stipendi.dao.CityDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.OccupationDAO;
import com.example.stipendi.model.City;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.Occupation;
import com.example.stipendi.excel.EmployeeExcelReader;
import com.example.stipendi.util.contract.ErrorHandler;

import java.util.*;

public class EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final CityDAO cityDAO;
    private final OccupationDAO occupationDAO;
    private final Set<String> newNkpdCodes;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.cityDAO = new CityDAO();
        this.occupationDAO = new OccupationDAO();
        this.newNkpdCodes = new HashSet<>();
    }

    public void importEmployeesFromExcel(String employeeFilePath, ErrorHandler errorHandler) {
        List<Employee> employees = EmployeeExcelReader.readEmployeesFromExcel(employeeFilePath, errorHandler);
        if (employees.isEmpty() && !errorHandler.hasErrors()) {
            errorHandler.addError("No employees found in the Excel file or the file format is incorrect.");
        }

        Map<String, Employee> uniqueEgnMap = new LinkedHashMap<>();
        for (Employee employee : employees) {
            String egn = employee.getEgn();
            if (uniqueEgnMap.containsKey(egn)) {
                Employee existingEmployee = uniqueEgnMap.get(egn);
                errorHandler.addError("Duplicate EGN found: " + egn + " - " + employee.getFullName() + " .");
                errorHandler.addError("First encountered NKPD (will be saved): " + existingEmployee.getOccupation().getNkpd());
                errorHandler.addError("Duplicate NKPD (will be ignored): " + employee.getOccupation().getNkpd());
                continue;
            }
            uniqueEgnMap.put(egn, employee);

            City city = cityDAO.getCityByName(employee.getCity().getCityName());
            if (city == null) {
                cityDAO.saveCity(employee.getCity());
                city = cityDAO.getCityByName(employee.getCity().getCityName());
            }
            employee.setCity(city);

            Occupation occupation = occupationDAO.getOccupationByNKPD(employee.getOccupation().getNkpd());
            if (occupation == null) {
                occupation = new Occupation();
                occupation.setNkpd(employee.getOccupation().getNkpd());
                occupationDAO.saveOccupation(occupation);
                newNkpdCodes.add(employee.getOccupation().getNkpd());
                occupation = occupationDAO.getOccupationByNKPD(employee.getOccupation().getNkpd());
            }
            employee.setOccupation(occupation);
        }

        for (Employee employee : uniqueEgnMap.values()) {
            employeeDAO.saveEmployee(employee);
        }

        if (!newNkpdCodes.isEmpty()) {
            StringBuilder message = new StringBuilder("The following new NKPD codes were added to the database without department and position information:\n");
            newNkpdCodes.forEach(nkpd -> message.append(nkpd).append("\n"));
            errorHandler.addError(message.toString());
        }
    }

    public void importAttendanceRecordsFromExcel(String attendanceFilePath, ErrorHandler errorHandler) {
        AttendanceService attendanceService = new AttendanceService();
        attendanceService.importAttendanceRecordsFromExcel(attendanceFilePath, errorHandler);
    }

    public void clearEmployeesTable() {
        employeeDAO.clearEmployeesTable();
    }
}
package com.example.stipendi.service;

import com.example.stipendi.dao.CityDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.OccupationDAO;
import com.example.stipendi.excel.AttendExcelReader;
import com.example.stipendi.model.City;
import com.example.stipendi.model.Employee;
import com.example.stipendi.model.Occupation;
import com.example.stipendi.excel.EmployeeExcelReader;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        for (Employee employee : employees) {
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

            employeeDAO.saveEmployee(employee);
        }

        errorHandler.displayErrors();

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


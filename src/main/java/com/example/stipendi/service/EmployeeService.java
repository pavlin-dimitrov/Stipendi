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
import java.util.function.Consumer;
import java.util.function.Supplier;

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
        List<Employee> employees = readEmployees(employeeFilePath, errorHandler);
        Map<String, Employee> uniqueEgnMap = processEmployees(employees, errorHandler);
        saveEmployees(uniqueEgnMap.values());
        reportNewNkpdCodes(errorHandler);
    }

    private List<Employee> readEmployees(String employeeFilePath, ErrorHandler errorHandler) {
        List<Employee> employees = EmployeeExcelReader.readEmployeesFromExcel(employeeFilePath, errorHandler);
        if (employees.isEmpty() && !errorHandler.hasErrors()) {
            errorHandler.addError("Файла е празен, неправилно форматиран или формата на файла не е xlsx или xls.");
        }
        return employees;
    }

    private Map<String, Employee> processEmployees(List<Employee> employees, ErrorHandler errorHandler) {
        Map<String, Employee> uniqueEgnMap = new LinkedHashMap<>();
        for (Employee employee : employees) {
            String egn = employee.getEgn();
            if (uniqueEgnMap.containsKey(egn)) {
                handleDuplicateEmployee(uniqueEgnMap.get(egn), employee, errorHandler);
            } else {
                uniqueEgnMap.put(egn, employee);
                processEmployeeData(employee);
            }
        }
        return uniqueEgnMap;
    }

    private void handleDuplicateEmployee(Employee existing, Employee duplicate, ErrorHandler errorHandler) {
        reportDuplicateError(existing, duplicate, errorHandler);
        updateEmployeeData(existing, duplicate);
    }

    private void reportDuplicateError(Employee existing, Employee duplicate, ErrorHandler errorHandler) {
        errorHandler.addError("Намерено е дублирано ЕГН: " + duplicate.getEgn() + " - " + duplicate.getFullName() + " .");
        errorHandler.addError("Първи код по НКПД за дублираното ЕГН: " + existing.getOccupation().getNkpd());
        errorHandler.addError("Втори код по НКПД за дублираното ЕГН (това ще бъде записано в базата): " + duplicate.getOccupation().getNkpd());
    }

    private void updateEmployeeData(Employee existing, Employee duplicate) {
        updateBaseSalary(existing, duplicate);
        updateDaysOff(existing, duplicate);
    }

    private void updateBaseSalary(Employee existing, Employee duplicate) {
        if (existing.getBaseSalary() == 0) {
            existing.setBaseSalary(duplicate.getBaseSalary());
        }
    }

    private void updateDaysOff(Employee existing, Employee duplicate) {
        updateDaysOffField(existing::getDaysOffDoo, existing::setDaysOffDoo, duplicate.getDaysOffDoo());
        updateDaysOffField(existing::getDaysOffEmpl, existing::setDaysOffEmpl, duplicate.getDaysOffEmpl());
    }

    private void updateDaysOffField(Supplier<Double> getter, Consumer<Double> setter, double newValue) {
        double existingValue = getter.get();
        if (existingValue == 0 && newValue != 0) {
            setter.accept(newValue);
        } else if (existingValue != 0 && newValue != 0) {
            setter.accept(existingValue + newValue);
        }
    }

    private void processEmployeeData(Employee employee) {
        updateCity(employee);
        updateOccupation(employee);
    }

    private void updateCity(Employee employee) {
        City city = cityDAO.getCityByName(employee.getCity().getCityName());
        if (city == null) {
            cityDAO.saveCity(employee.getCity());
            city = cityDAO.getCityByName(employee.getCity().getCityName());
        }
        employee.setCity(city);
    }

    private void updateOccupation(Employee employee) {
        Occupation occupation = occupationDAO.getOccupationByNKPD(employee.getOccupation().getNkpd());
        if (occupation == null) {
            occupation = createNewOccupation(employee.getOccupation().getNkpd());
        }
        employee.setOccupation(occupation);
    }

    private Occupation createNewOccupation(String nkpd) {
        Occupation occupation = new Occupation();
        occupation.setNkpd(nkpd);
        occupationDAO.saveOccupation(occupation);
        newNkpdCodes.add(nkpd);
        return occupationDAO.getOccupationByNKPD(nkpd);
    }

    private void saveEmployees(Collection<Employee> employees) {
        employees.forEach(employeeDAO::saveEmployee);
    }

    private void reportNewNkpdCodes(ErrorHandler errorHandler) {
        if (!newNkpdCodes.isEmpty()) {
            String message = "The following new NKPD codes were added to the database without department and position information:\n" +
                    String.join("\n", newNkpdCodes);
            errorHandler.addError(message);
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
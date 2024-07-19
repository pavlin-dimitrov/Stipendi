package com.example.stipendi;

import com.example.stipendi.controller.MainController;
import com.example.stipendi.dao.AppConfigVariableDAO;
import com.example.stipendi.dao.DirectlyOccupiedDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.IndirectOccupiedDAO;
import com.example.stipendi.excel.ExcelEmployeeReporter;
import com.example.stipendi.excel.ExcelWorkTimeReport;
import com.example.stipendi.model.AppConfigVariable;
import com.example.stipendi.service.DirectIndirectService;
import com.example.stipendi.service.EmployeeService;
import com.example.stipendi.service.SalaryService;
import com.example.stipendi.util.WorkdayCalculator;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // Add the CSS file

        // Получаване на контролера и задаване на услугите
        MainController controller = loader.getController();
        EmployeeService employeeService = new EmployeeService();
        ErrorHandler errorHandler = new ErrorHandlerImpl(controller.getErrorTextArea());
        EmployeeDAO employeeDAO = new EmployeeDAO();
        WorkdayCalculator workdayCalculator = new WorkdayCalculator();
        AppConfigVariableDAO appConfigVariableDAO = new AppConfigVariableDAO();
        SalaryService salaryService = new SalaryService(employeeDAO, workdayCalculator,appConfigVariableDAO);
        ExcelEmployeeReporter excelEmployeeReporter = new ExcelEmployeeReporter(employeeDAO);
        controller.setServices(employeeService, errorHandler, salaryService, excelEmployeeReporter);
        controller.cleanEmployeeTable();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Stipendi Application");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

//        EmployeeService employeeService = new EmployeeService();
//
//        WorkdayCalculator workdayCalculator = new WorkdayCalculator();
//        AppConfigVariableDAO appConfigVariableDAO = new AppConfigVariableDAO();
//        SalaryService salaryService = new SalaryService(new EmployeeDAO(), workdayCalculator, appConfigVariableDAO);
//        DirectIndirectService directIndirectService = new DirectIndirectService(new EmployeeDAO(), new DirectlyOccupiedDAO(), new IndirectOccupiedDAO());
//
//        String employeeFilePath = "C:\\Users\\Pavlin\\Downloads\\rbo\\TEMPO DI LAVORO 2024.04.xls";
//        String attendanceFilePath = "C:\\Users\\Pavlin\\Downloads\\rbo\\WorkTime_Ver2 Производство 1 пример.xlsx";
//
//        employeeService.importEmployeesFromExcel(employeeFilePath);
//        System.out.println("Employees have been imported successfully!");
//
//        employeeService.importAttendanceRecordsFromExcel(attendanceFilePath);
//        System.out.println("Attendance records have been imported and updated successfully!");

        // TODO: Implement JavaFX UI

        //salaryService.updateProfessionalExperienceBonus();
        //System.out.println("Professional experience bonuses have been updated successfully!");

//        salaryService.updateOneTimeBonus(5, 2024);
//        System.out.println("One-time bonuses have been calculated successfully!");
//        System.out.println(workdayCalculator.getWorkdaysInMonth(5, 2024));

        //salaryService.updateTransportBonus();
        //System.out.println("Transport bonus updated successfully");

        //salaryService.updateFinalSalary();
        //System.out.println("Final salary updated successfully");

        //directIndirectService.updateDirectlyOccupied(5, 2024);
        //System.out.println("Directly occupied hours calculated");

//        directIndirectService.updateIndirectOccupied(5, 2024);
//        System.out.println("Indirect occupied hours calculated");

        //ExcelEmployeeReporter reporter = new ExcelEmployeeReporter(new EmployeeDAO());
       // reporter.generateEmployeeReport("C:\\Users\\Pavlin\\Downloads\\rbo\\reports\\EmployeeReport.xlsx");

//        ErrorHandler errorHandler = new ErrorHandlerImpl();
//        ExcelWorkTimeReport excelWorkTimeReport = new ExcelWorkTimeReport(new DirectlyOccupiedDAO(), new IndirectOccupiedDAO(), errorHandler);
//        excelWorkTimeReport.generateWorkHoursReport("C:\\Users\\Pavlin\\Downloads\\rbo\\reports\\Work Hours Report.xlsx");
    }
}
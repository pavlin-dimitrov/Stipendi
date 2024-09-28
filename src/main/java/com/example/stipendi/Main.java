package com.example.stipendi;

import com.example.stipendi.controller.MainController;
import com.example.stipendi.dao.AppConfigVariableDAO;
import com.example.stipendi.dao.DirectlyDAO;
import com.example.stipendi.dao.EmployeeDAO;
import com.example.stipendi.dao.IndirectDAO;
import com.example.stipendi.excel.ExcelEmployeeReporter;
import com.example.stipendi.excel.ExcelWorkTimeReport;
import com.example.stipendi.service.DirectIndirectService;
import com.example.stipendi.service.EmployeeService;
import com.example.stipendi.service.SalaryService;
import com.example.stipendi.util.WorkdayCalculator;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane root = loader.load();
            Scene mainScene = new Scene(root, 800, 600);
            mainScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // Add the CSS file

            // Получаване на контролера и задаване на услугите
            MainController controller = loader.getController();
            controller.setStageAndScene(primaryStage, mainScene);

            EmployeeService employeeService = new EmployeeService();
            ErrorHandler errorHandler = new ErrorHandlerImpl(controller.getErrorTextArea());
            EmployeeDAO employeeDAO = new EmployeeDAO();
            WorkdayCalculator workdayCalculator = new WorkdayCalculator();
            AppConfigVariableDAO appConfigVariableDAO = new AppConfigVariableDAO();
            SalaryService salaryService = new SalaryService(employeeDAO, workdayCalculator, appConfigVariableDAO);
            ExcelEmployeeReporter excelEmployeeReporter = new ExcelEmployeeReporter(employeeDAO);
            DirectlyDAO directlyDAO = new DirectlyDAO();
            IndirectDAO indirectDAO = new IndirectDAO();
            ExcelWorkTimeReport excelWorkTimeReport = new ExcelWorkTimeReport(directlyDAO, indirectDAO, errorHandler);
            DirectIndirectService directIndirectService = new DirectIndirectService(employeeDAO, directlyDAO, indirectDAO);

            controller.setServices(employeeService,
                    errorHandler,
                    salaryService,
                    excelEmployeeReporter,
                    excelWorkTimeReport,
                    directIndirectService);

            controller.cleanEmployeeTable();

            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Stipendi Application");
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
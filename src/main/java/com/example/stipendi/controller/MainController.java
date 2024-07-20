package com.example.stipendi.controller;

import com.example.stipendi.excel.ExcelEmployeeReporter;
import com.example.stipendi.excel.ExcelWorkTimeReport;
import com.example.stipendi.service.DirectIndirectService;
import com.example.stipendi.service.EmployeeService;
import com.example.stipendi.service.SalaryService;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.time.YearMonth;

public class MainController {
    private EmployeeService employeeService;
    private ExcelEmployeeReporter excelEmployeeReporter;
    private ErrorHandler errorHandler;
    private SalaryService salaryService;
    private ExcelWorkTimeReport excelWorkTimeReport;
    private DirectIndirectService directIndirectService;

    private Stage stage;
    private Scene mainScene;

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    public MainController() {
        // Default constructor
    }

    public void cleanEmployeeTable(){
        employeeService.clearEmployeesTable();
    }

    public void setServices(EmployeeService employeeService,
                            ErrorHandler errorHandler,
                            SalaryService salaryService,
                            ExcelEmployeeReporter excelEmployeeReporter,
                            ExcelWorkTimeReport excelWorkTimeReport,
                            DirectIndirectService directIndirectService) {
        this.employeeService = employeeService;
        this.errorHandler = errorHandler;
        this.salaryService = salaryService;
        this.excelEmployeeReporter = excelEmployeeReporter;
        this.excelWorkTimeReport = excelWorkTimeReport;
        this.directIndirectService = directIndirectService;
    }

    @FXML
    private Button selectEmployeeFileButton;

    @FXML
    private Button selectAttendanceFileButton;

    @FXML
    private Button selectFolderButton;

    @FXML
    private Button calculateButton;

    @FXML
    private Button generateReportButton;

    @FXML
    private Button generateReportDirettiIndiretti;

    @FXML
    private TextField employeeFilePath;

    @FXML
    private TextField attendanceFilePath;

    @FXML
    private TextField reportsFolderPath;

    @FXML
    private DatePicker monthYearPicker;

    @Getter
    @FXML
    private TextArea errorTextArea;

    @FXML
    private Label errorLabel;

    @FXML
    private Button appConfigVariables;

    @FXML
    public void initialize() {
        selectEmployeeFileButton.setOnAction(event -> selectEmployeeFile());
        selectAttendanceFileButton.setOnAction(event -> selectAttendanceFile());
        selectFolderButton.setOnAction(event -> selectReportsFolder());
        calculateButton.setOnAction(actionEvent -> calculate());
        generateReportButton.setOnAction(actionEvent -> generateReport());
        generateReportDirettiIndiretti.setOnAction(actionEvent -> generateReportDirettiIndiretti());
        appConfigVariables.setOnAction(actionEvent -> navigateToVariablesScene());

        this.errorHandler = new ErrorHandlerImpl(errorTextArea); // Initialize error handler with text area
    }

    private void selectEmployeeFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select TRZ File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            employeeFilePath.setText(file.getAbsolutePath());
            employeeService.importEmployeesFromExcel(employeeFilePath.getText(), errorHandler);
        }
    }

    private void selectAttendanceFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Check-In File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            attendanceFilePath.setText(file.getAbsolutePath());
            employeeService.importAttendanceRecordsFromExcel(attendanceFilePath.getText(), errorHandler);
        }
    }

    private void selectReportsFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Reports Folder");
        File folder = directoryChooser.showDialog(new Stage());
        if (folder != null) {
            reportsFolderPath.setText(folder.getAbsolutePath());
        }
    }

    private void calculate() {
        if (employeeFilePath.getText().isEmpty() || attendanceFilePath.getText().isEmpty() || monthYearPicker.getValue() == null) {
            errorLabel.setText("Please select both files and a date.");
            return;
        }

        YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();

        salaryService.updateEmployeeSalary(month, year);
        directIndirectService.updateIndirectOccupied(month, year);
        directIndirectService.updateDirectlyOccupied(month, year);
    }

    private void generateReport() {
        YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();
        excelEmployeeReporter.generateEmployeeReport(reportsFolderPath.getText(), month, year);
    }

    private void generateReportDirettiIndiretti(){
        YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();
        excelWorkTimeReport.generateWorkHoursReport(reportsFolderPath.getText(), month, year);
    }

    private void navigateToVariablesScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/variables.fxml"));
            Scene variablesScene = new Scene(loader.load(), 800, 600);

            VariablesController variablesController = loader.getController();
            variablesController.setStageAndScene(stage, mainScene);

            stage.setScene(variablesScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




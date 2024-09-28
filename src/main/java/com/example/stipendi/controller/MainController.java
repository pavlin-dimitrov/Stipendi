package com.example.stipendi.controller;

import com.example.stipendi.excel.ExcelEmployeeReporter;
import com.example.stipendi.excel.ExcelWorkTimeReport;
import com.example.stipendi.service.DirectIndirectService;
import com.example.stipendi.service.EmployeeService;
import com.example.stipendi.service.SalaryService;
import com.example.stipendi.util.contract.ErrorHandler;
import com.example.stipendi.util.implementation.ErrorHandlerImpl;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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
    }

    public void cleanEmployeeTable() {
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
    private Button city;

    @FXML
    private Button holidays;

    @FXML
    private Button occupation;

    @FXML
    private Button helpButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        selectEmployeeFileButton.setOnAction(event -> selectEmployeeFile());
        selectAttendanceFileButton.setOnAction(event -> selectAttendanceFile());
        selectFolderButton.setOnAction(event -> selectReportsFolder());
        calculateButton.setOnAction(actionEvent -> calculate());
        generateReportButton.setOnAction(actionEvent -> generateReport());
        generateReportDirettiIndiretti.setOnAction(actionEvent -> generateReportDirettiIndiretti());
        appConfigVariables.setOnAction(actionEvent -> navigateToVariablesScene());
        city.setOnAction(actionEvent -> navigateToCityScene());
        holidays.setOnAction(actionEvent -> navigateToHolidaysScene());
        occupation.setOnAction(actionEvent -> navigateToOccupation());
        helpButton.setOnAction(actionEvent -> openHelpPage());

        this.errorHandler = new ErrorHandlerImpl(errorTextArea); // Initialize error handler with text area
    }

    private void selectEmployeeFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select TRZ File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            employeeFilePath.setText(file.getAbsolutePath());
            errorHandler.clearErrors(); // Clear previous errors
            employeeService.importEmployeesFromExcel(employeeFilePath.getText(), errorHandler);
            if (!errorHandler.hasErrors()) {
                showAlert("Success", "Employees imported successfully.", Alert.AlertType.INFORMATION);
            }
        }
    }

    private void selectAttendanceFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Check-In File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            attendanceFilePath.setText(file.getAbsolutePath());
            errorHandler.clearErrors(); // Clear previous errors
            employeeService.importAttendanceRecordsFromExcel(attendanceFilePath.getText(), errorHandler);
            if (!errorHandler.hasErrors()) {
                showAlert("Success", "Attendance records imported successfully.", Alert.AlertType.INFORMATION);
            }
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

//    private void calculate() {
//        if (employeeFilePath.getText().isEmpty() || attendanceFilePath.getText().isEmpty() || monthYearPicker.getValue() == null) {
//            errorLabel.setText("Please select both files and a date.");
//            return;
//        }
//
//        YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
//        int month = selectedDate.getMonthValue();
//        int year = selectedDate.getYear();
//
//        errorHandler.clearErrors();
//        salaryService.updateEmployeeSalary(month, year, errorHandler);
//        directIndirectService.updateIndirectOccupied(month, year);
//        directIndirectService.updateDirectlyOccupied(month, year);
//    }

    @FXML
    private void calculate() {
        if (employeeFilePath.getText().isEmpty() || attendanceFilePath.getText().isEmpty() || monthYearPicker.getValue() == null) {
            errorLabel.setText("Please select both files and a date.");
            return;
        }

        YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
        int month = selectedDate.getMonthValue();
        int year = selectedDate.getYear();

        // Създаване на задача за асинхронно изпълнение на изчисленията
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Показваме прогрес бара
                updateProgress(0, 3);  // Прогресът е от 0 до 3 (за 3 операции)

                // Изчистваме грешките и започваме изчисленията
                errorHandler.clearErrors();
                salaryService.updateEmployeeSalary(month, year, errorHandler);
                updateProgress(1, 3);  // 1/3 от задачите изпълнени

                directIndirectService.updateIndirectOccupied(month, year);
                updateProgress(2, 3);  // 2/3 от задачите изпълнени

                directIndirectService.updateDirectlyOccupied(month, year);
                updateProgress(3, 3);  // Всички задачи изпълнени

                return null;
            }

            @Override
            protected void succeeded() {
                // Скриваме прогрес бара при успешни изчисления
                progressBar.setVisible(false);
                errorLabel.setText("Изчисленията са успешно завършени.");
            }

            @Override
            protected void failed() {
                // Скриваме прогрес бара при грешка
                progressBar.setVisible(false);
                errorLabel.setText("Възникна грешка по време на изчисленията.");
            }
        };

        // Свързване на прогрес бара с задачата
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);  // Показваме прогрес бара

        // Изпълняваме задачата в нов поток, за да не блокираме UI-то
        new Thread(task).start();
    }

    private void generateReport() {
        if (employeeFilePath.getText().isEmpty() || attendanceFilePath.getText().isEmpty() || reportsFolderPath.getText().isEmpty()) {
            errorTextArea.setText("Не са попълнени всички полета. Попълнете и опитайте отново");
        } else {
            YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
            int month = selectedDate.getMonthValue();
            int year = selectedDate.getYear();
            String result = excelEmployeeReporter.generateEmployeeReport(reportsFolderPath.getText(), month, year);
            errorTextArea.setText(result);
        }
    }

    private void generateReportDirettiIndiretti() {
        if (employeeFilePath.getText().isEmpty() || attendanceFilePath.getText().isEmpty() || reportsFolderPath.getText().isEmpty()) {
            errorTextArea.setText("Не са попълнени всички полета. Попълнете и опитайте отново");
        } else {
            YearMonth selectedDate = YearMonth.from(monthYearPicker.getValue());
            int month = selectedDate.getMonthValue();
            int year = selectedDate.getYear();
            excelWorkTimeReport.generateWorkHoursReport(reportsFolderPath.getText(), month, year);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToVariablesScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/variables.fxml"));
            Scene variablesScene = new Scene(loader.load(), 800, 600);
            variablesScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            VariablesController variablesController = loader.getController();
            variablesController.setStageAndScene(stage, mainScene);

            stage.setScene(variablesScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToCityScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/city.fxml"));
            Scene cityScene = new Scene(loader.load(), 800, 600);
            cityScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            CityController cityController = loader.getController();
            cityController.setStageAndScene(stage, mainScene);

            stage.setScene(cityScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToHolidaysScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/holidays.fxml"));
            Scene holidaysScene = new Scene(loader.load(), 800, 600);
            holidaysScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            HolidaysController holidaysController = loader.getController();
            holidaysController.setStageAndScene(stage, mainScene);

            stage.setScene(holidaysScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToOccupation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/occupation.fxml"));
            Scene occupationScene = new Scene(loader.load(), 800, 600);
            occupationScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            OccupationController occupationController = loader.getController();
            occupationController.setStageAndScene(stage, mainScene);

            stage.setScene(occupationScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openHelpPage() {
        try {
            URL helpFileUrl = getClass().getResource("/help.html");
            if (helpFileUrl == null) {
                showAlert("Error", "Help file not found!", Alert.AlertType.ERROR);
                return;
            }

            File helpFile = new File(helpFileUrl.toURI());
            String path = helpFile.getAbsolutePath();

            // Използвайте системна команда за стартиране на браузъра
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "start", path});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", path});
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"xdg-open", path});
            } else {
                showAlert("Error", "Unsupported operating system!", Alert.AlertType.ERROR);
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open help page!", Alert.AlertType.ERROR);
        }
    }
}
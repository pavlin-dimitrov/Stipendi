package com.example.stipendi.controller;

import com.example.stipendi.dao.HolidayDAO;
import com.example.stipendi.model.Holiday;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class HolidaysController {

    @FXML private TableView<Holiday> holidaysTable;
    @FXML private TableColumn<Holiday, Integer> monthColumn;
    @FXML private TableColumn<Holiday, Integer> holidaysColumn;
    private ObservableList<Holiday> holidaysList;
    @FXML private TextField monthField;
    @FXML private TextField numHolidaysField;
    @FXML private Button btnUpdate;
    @FXML private Label errorLabel;
    private Stage stage;
    private Scene mainScene;
    private HolidayDAO holidayDAO;

    private Holiday selectedHoliday;

    public HolidaysController() {
        this.holidayDAO = new HolidayDAO();
    }

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    @FXML
    private void initialize() {
        initializeTable();
        loadTableData();
        setupTableSelection();
        btnUpdate.setDisable(true);
        monthField.setDisable(true);
    }

    private Holiday lastSelectedHoliday = null;

    private void setupTableSelection() {
        holidaysTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        holidaysTable.setOnMouseClicked(event -> {
            int clickedIndex = holidaysTable.getSelectionModel().getSelectedIndex();

            if (clickedIndex != -1) {
                Holiday clickedHoliday = holidaysTable.getItems().get(clickedIndex);

                if (clickedHoliday.equals(lastSelectedHoliday)) {
                    holidaysTable.getSelectionModel().clearSelection();
                    clearSelection();
                    lastSelectedHoliday = null;
                } else {
                    selectedHoliday = clickedHoliday;
                    monthField.setText(getMonthName(selectedHoliday.getMonthNum()));
                    numHolidaysField.setText(String.valueOf(selectedHoliday.getHolidayNum()));
                    btnUpdate.setDisable(false);
                    lastSelectedHoliday = selectedHoliday;
                }
            }
        });

        // Добавяме слушатели за промени в текстовите полета
        monthField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });

        numHolidaysField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });
    }

    private void checkFieldsAndUpdateSelection() {
        if (monthField.getText().isEmpty() && numHolidaysField.getText().isEmpty()) {
            holidaysTable.getSelectionModel().clearSelection();
            clearSelection();
            lastSelectedHoliday = null;
            btnUpdate.setDisable(true);
        }
    }

    private void clearSelection() {
        selectedHoliday = null;
        monthField.clear();
        numHolidaysField.clear();
        btnUpdate.setDisable(true);
    }

    private void initializeTable() {
        monthColumn.setCellValueFactory(new PropertyValueFactory<>("monthNum"));
        holidaysColumn.setCellValueFactory(new PropertyValueFactory<>("holidayNum"));

        monthColumn.setCellFactory(column -> new TableCell<Holiday, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(getMonthName(item));
                }
            }
        });

        monthColumn.setComparator(Integer::compare);

        holidaysList = FXCollections.observableArrayList();
        holidaysTable.setItems(holidaysList);
    }

    private String getMonthName(Integer monthNumber) {
        String[] monthNames = {"Януари", "Февруари", "Март", "Април", "Май", "Юни",
                "Юли", "Август", "Септември", "Октомври", "Ноември", "Декември"};
        return monthNames[monthNumber - 1];
    }

    private void loadTableData() {
        List<Holiday> holidays = holidayDAO.getAllHolidays();
        holidaysList.clear();
        holidaysList.addAll(holidays);
    }

    @FXML
    private void handleUpdateHoliday() {
        if (selectedHoliday == null) {
            errorLabel.setText("Моля, изберете месец за обновяване.");
            return;
        }

        if (numHolidaysField.getText().isEmpty()) {
            errorLabel.setText("Моля, въведете брой празници.");
            return;
        }

        if (!isInteger(numHolidaysField.getText())) {
            errorLabel.setText("Моля, въведете валидно цяло число за брой празници.");
            return;
        }

        try {
            selectedHoliday.setHolidayNum(Integer.parseInt(numHolidaysField.getText()));

            holidayDAO.updateHoliday(selectedHoliday);

            errorLabel.setText("");
            loadTableData();
            holidaysTable.refresh();
        } catch (Exception e) {
            errorLabel.setText("Възникна грешка при обновяването. Моля, опитайте отново.");
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleBackButton() {
        stage.setScene(mainScene);
    }
}
package com.example.stipendi.controller;

import com.example.stipendi.dao.CityDAO;
import com.example.stipendi.model.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class CityController {

    @FXML private TableView<City> cityTable;
    @FXML private TableColumn<City, String> cityNameColumn;
    @FXML private TableColumn<City, Double> distanceColumn;
    private ObservableList<City> cityList;
    @FXML private TextField cityNameField;
    @FXML private TextField distanceField;
    @FXML private Button btnAdd;
    @FXML private Label errorLabel;
    @FXML private Button btnUpdate;
    @FXML Button btnDelete;
    private City selectedCity;
    private Stage stage;
    private Scene mainScene;
    private CityDAO cityDAO;
    private City lastSelectedCity = null;

    public CityController() {
        this.cityDAO = new CityDAO();
    }

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    @FXML
    private void initialize() {
        loadCity();
        initializeTable();
        loadTableData();
        setupTableSelection();
        btnUpdate.setDisable(true);
    }

    private void setupTableSelection() {
        cityTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        cityTable.setOnMouseClicked(event -> {
            int clickedIndex = cityTable.getSelectionModel().getSelectedIndex();

            if (clickedIndex != -1) {
                City clickedCity = cityTable.getItems().get(clickedIndex);

                if (clickedCity.equals(lastSelectedCity)) {
                    cityTable.getSelectionModel().clearSelection();
                    clearSelection();
                    lastSelectedCity = null;
                } else {
                    selectedCity = clickedCity;
                    cityNameField.setText(selectedCity.getCityName());
                    distanceField.setText(String.valueOf(selectedCity.getDistance()));
                    btnAdd.setDisable(true);
                    btnUpdate.setDisable(false);
                    lastSelectedCity = selectedCity;
                }
            }
        });

        // Добавяме слушатели за промени в текстовите полета
        cityNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });

        distanceField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });
    }

    private void checkFieldsAndUpdateSelection() {
        if (cityNameField.getText().isEmpty() && distanceField.getText().isEmpty()) {
            cityTable.getSelectionModel().clearSelection();
            clearSelection();
            lastSelectedCity = null;
            btnAdd.setDisable(false);
            btnUpdate.setDisable(true);
        }
    }

    private void clearSelection() {
        selectedCity = null;
        cityNameField.clear();
        distanceField.clear();
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
    }

    private void loadCity() {
        btnAdd.setOnAction(actionEvent -> handleAddCity());
        btnUpdate.setOnAction(actionEvent -> handleUpdateCity());
        btnDelete.setOnAction(actionEvent -> handleDeleteCity());
    }

    private void initializeTable() {
        cityNameColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));

        cityList = FXCollections.observableArrayList();
        cityTable.setItems(cityList);
    }

    private void loadTableData() {
        List<City> cities = cityDAO.getAllCities();
        cityList.clear();
        cityList.addAll(cities);
    }

    @FXML
    private void handleAddCity() {
        if (cityNameField.getText().isEmpty() || distanceField.getText().isEmpty()) {
            errorLabel.setText("Please, enter both city and distance.");
            return;
        }

        if (!isDouble(distanceField.getText())) {
            errorLabel.setText("Please, enter only numbers in the distance field.");
            return;
        }

        try {
            City city = new City();
            city.setCityName(cityNameField.getText());
            double distance = Double.parseDouble(distanceField.getText());
            city.setDistance(distance);

            boolean success = cityDAO.saveCity(city);

            if (success) {
                clearSelection();
                loadTableData();
            } else {
                errorLabel.setText("City already exists!");
            }
        } catch (Exception e) {
            errorLabel.setText("Error occurred! Try again.");
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    private void handleUpdateCity() {
        if (selectedCity == null) {
            errorLabel.setText("Please select a city to update.");
            return;
        }

        if (cityNameField.getText().isEmpty() || distanceField.getText().isEmpty()) {
            errorLabel.setText("Please enter both city name and distance.");
            return;
        }

        if (!isDouble(distanceField.getText())) {
            errorLabel.setText("Please enter only numbers in the distance field.");
            return;
        }

        try {
            selectedCity.setCityName(cityNameField.getText());
            selectedCity.setDistance(Double.parseDouble(distanceField.getText()));

            cityDAO.updateCity(selectedCity);

            clearSelection();
            loadTableData();
        } catch (Exception e) {
            errorLabel.setText("An error occurred during update. Please try again.");
        }
    }

    @FXML
    private void handleDeleteCity() {
        if (selectedCity == null) {
            errorLabel.setText("Моля, изберете град за изтриване.");
            return;
        }

        try {
            // Проверка за свързани служители
            int employeeCount = cityDAO.getEmployeeCountByCity(selectedCity.getId());

            if (employeeCount > 0) {
                // Ако има свързани служители, покажи съобщение на потребителя
                errorLabel.setText("Градът не може да бъде изтрит, докато има свързани служители. Моля, рестартирайте приложението. Това ще нулира базата данни.");
                return;
            }

            // Ако няма свързани служители, продължи с изтриването
            cityDAO.deleteCity(selectedCity.getId());

            cityNameField.clear();
            distanceField.clear();
            errorLabel.setText("Градът е успешно изтрит.");
            loadTableData();
            btnAdd.setDisable(false);
            btnUpdate.setDisable(true);
            selectedCity = null;
        } catch (Exception e) {
            errorLabel.setText("Възникна грешка при изтриването. Моля, опитайте отново.");
        }
    }

    @FXML
    private void handleBackButton() {
        stage.setScene(mainScene);
    }
}

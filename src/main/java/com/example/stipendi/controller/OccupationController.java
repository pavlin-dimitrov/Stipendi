package com.example.stipendi.controller;

import com.example.stipendi.dao.OccupationDAO;
import com.example.stipendi.model.Occupation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class OccupationController {

    @FXML private TableView<Occupation> occupationTable;
    @FXML private TableColumn<Occupation, Integer> idColumn;
    @FXML private TableColumn<Occupation, String> nkpdColumn;
    @FXML private TableColumn<Occupation, String> departmentColumn;
    @FXML private TableColumn<Occupation, String> positionColumn;
    @FXML private TextField nkpdField;
    @FXML private TextField departmentField;
    @FXML private TextField positionField;
    @FXML private Label errorLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    private OccupationDAO occupationDAO;
    private ObservableList<Occupation> occupationList;
    private Stage stage;
    private Scene mainScene;
    private Occupation lastSelectedOccupation = null;

    public OccupationController() {
        this.occupationDAO = new OccupationDAO();
        this.occupationList = FXCollections.observableArrayList();
    }

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nkpdColumn.setCellValueFactory(new PropertyValueFactory<>("nkpd"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        btnAdd.setOnAction(actionEvent -> handleAddOccupation());
        btnUpdate.setOnAction(actionEvent -> handleUpdateOccupation());
        btnDelete.setOnAction(actionEvent -> handleDeleteOccupation());
        loadOccupationData();
        setupTableSelection();
    }

    private void loadOccupationData() {
        occupationList.setAll(occupationDAO.getAllOccupations());
        occupationTable.setItems(occupationList);
        btnUpdate.setDisable(true);
    }

    private void setupTableSelection() {
        occupationTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        occupationTable.setOnMouseClicked(event -> {
            int clickedIndex = occupationTable.getSelectionModel().getSelectedIndex();

            if (clickedIndex != -1) {
                Occupation clickedOccupation = occupationTable.getItems().get(clickedIndex);

                if (clickedOccupation.equals(lastSelectedOccupation)) {
                    occupationTable.getSelectionModel().clearSelection();
                    clearSelection();
                    lastSelectedOccupation = null;
                } else {
                    nkpdField.setText(clickedOccupation.getNkpd());
                    departmentField.setText(clickedOccupation.getDepartment());
                    positionField.setText(clickedOccupation.getPosition());
                    btnAdd.setDisable(true);
                    btnUpdate.setDisable(false);
                    lastSelectedOccupation = clickedOccupation;
                }
            }
        });

        // Добавяме слушатели за промени в текстовите полета
        nkpdField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });

        departmentField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });

        positionField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkFieldsAndUpdateSelection();
        });
    }

    private void checkFieldsAndUpdateSelection() {
        if (nkpdField.getText().isEmpty() && departmentField.getText().isEmpty() && positionField.getText().isEmpty()) {
            occupationTable.getSelectionModel().clearSelection();
            clearSelection();
            lastSelectedOccupation = null;
        }
    }

    private void clearSelection() {
        occupationTable.getSelectionModel().clearSelection();
        nkpdField.clear();
        departmentField.clear();
        positionField.clear();
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        errorLabel.setText("");
    }

    @FXML
    private void handleAddOccupation() {
        String nkpd = nkpdField.getText();
        String department = departmentField.getText();
        String position = positionField.getText();

        if (nkpd == null || nkpd.trim().isEmpty()) {
            errorLabel.setText("NKPD cannot be empty.");
            return;
        }

        Occupation newOccupation = new Occupation();
        newOccupation.setNkpd(nkpd);
        newOccupation.setDepartment(department);
        newOccupation.setPosition(position);
        occupationDAO.saveOccupation(newOccupation);
        loadOccupationData();
        clearForm();
    }

    @FXML
    private void handleUpdateOccupation() {
        Occupation selectedOccupation = occupationTable.getSelectionModel().getSelectedItem();
        if (selectedOccupation == null) {
            errorLabel.setText("No occupation selected.");
            return;
        }

        String nkpd = nkpdField.getText();
        String department = departmentField.getText();
        String position = positionField.getText();

        if (nkpd == null || nkpd.trim().isEmpty()) {
            errorLabel.setText("NKPD cannot be empty.");
            return;
        }

        selectedOccupation.setNkpd(nkpd);
        selectedOccupation.setDepartment(department);
        selectedOccupation.setPosition(position);
        occupationDAO.updateOccupation(selectedOccupation);
        loadOccupationData();
        clearForm();
    }

    @FXML
    private void handleDeleteOccupation() {
        Occupation selectedOccupation = occupationTable.getSelectionModel().getSelectedItem();
        if (selectedOccupation == null) {
            errorLabel.setText("No occupation selected.");
            return;
        }
        occupationDAO.deleteOccupation(selectedOccupation.getId());
        loadOccupationData();
        clearForm();
    }

    private void clearForm() {
        nkpdField.clear();
        departmentField.clear();
        positionField.clear();
        errorLabel.setText("");
    }

    @FXML
    private void handleBackButton() {
        stage.setScene(mainScene);
    }
}
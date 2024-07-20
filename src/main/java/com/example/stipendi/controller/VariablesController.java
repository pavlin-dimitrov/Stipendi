package com.example.stipendi.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VariablesController {

    @FXML
    private Label label;

    @FXML
    private TextField textField;

    @FXML
    private Button submitButton;

    private Stage stage;
    private Scene mainScene;

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    @FXML
    private void handleSubmitButtonOvertimeWeek() {
        // Implement submit button logic here
    }

    @FXML
    private void handleSubmitButtonOvertimeWeekend() {
        // Implement submit button logic here
    }

    @FXML
    private void handleSubmitButtonightShift() {
        // Implement submit button logic here
    }

    @FXML
    private void handleSubmitButtonFuel() {
        // Implement submit button logic here
    }

    @FXML
    private void handleSubmitButtonContributi() {
        // Implement submit button logic here
    }

    @FXML
    private void handleSubmitButtonDdfl() {
        // Implement submit button logic here
    }

    @FXML
    private void handleBackButton() {
        stage.setScene(mainScene);
    }
}


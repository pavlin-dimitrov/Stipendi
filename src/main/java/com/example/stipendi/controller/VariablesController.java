package com.example.stipendi.controller;

import com.example.stipendi.dao.AppConfigVariableDAO;
import com.example.stipendi.model.AppConfigVariable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VariablesController {

    @FXML private TextField txtOvertimeWeek;
    @FXML private Label lblOvertimeWeek;
    @FXML private TextField txtOvertimeWeekend;
    @FXML private Label lblOvertimeWeekend;
    @FXML private TextField txtNightShift;
    @FXML private Label lblNightShift;
    @FXML private TextField txtFuel;
    @FXML private Label lblFuel;
    @FXML private TextField txtContributi;
    @FXML private Label lblContributi;
    @FXML private TextField txtDdfl;
    @FXML private Label lblDdfl;
    @FXML private Button submitOvertimeWeek;
    @FXML private Button submitOvertimeWeekend;
    @FXML private Button submitNightShift;
    @FXML private Button submitFuel;
    @FXML private Button submitContributi;
    @FXML private Button submitDdfl;
    @FXML private Button submitAchievementBonus;
    @FXML private Button submitCutAchievementBonus;
    @FXML private Label lblAchievementBonus;
    @FXML private Label lblCutAchievementBonus;
    @FXML private TextField txtAchievementBonus;
    @FXML private TextField txtCutAchievementBonus;
    private Stage stage;
    private Scene mainScene;
    private AppConfigVariableDAO appConfigVariableDAO;

    public VariablesController() {
        appConfigVariableDAO = new AppConfigVariableDAO();
    }

    public void setStageAndScene(Stage stage, Scene mainScene) {
        this.stage = stage;
        this.mainScene = mainScene;
    }

    @FXML private void initialize() {
        loadConfigVariables();
    }

    private void loadConfigVariables() {
        lblOvertimeWeek.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("overtimeWeek")));
        lblOvertimeWeekend.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("overtimeWeekend")));
        lblNightShift.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("nightShift")));
        lblFuel.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("fuel")));
        lblContributi.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("contributi")));
        lblDdfl.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("ddfl")));
        lblAchievementBonus.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("achievementBonus")));
        lblCutAchievementBonus.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("partOfAchievementBonus")));
        submitOvertimeWeek.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("overtimeWeek")));
        submitOvertimeWeekend.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("overtimeWeekend")));
        submitNightShift.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("nightShift")));
        submitFuel.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("fuel")));
        submitContributi.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("contributi")));
        submitDdfl.setOnAction(actionEvent -> appConfigVariableDAO.
                updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("ddfl")));
        submitAchievementBonus.setOnAction(actionEvent -> appConfigVariableDAO
                .updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("achievementBonus")));
        submitCutAchievementBonus.setOnAction(actionEvent -> appConfigVariableDAO
                .updateAppConfigVariable(appConfigVariableDAO.getAppConfigVariableByName("partOfAchievementBonus")));
    }

    @FXML private void handleSubmitButtonOvertimeWeek() {
        updateConfigVariable("Straordinari setimaniali", txtOvertimeWeek.getText(), lblOvertimeWeek);
    }

    @FXML private void handleSubmitButtonOvertimeWeekend() {
        updateConfigVariable("Straordinari weekend", txtOvertimeWeekend.getText(), lblOvertimeWeekend);
    }

    @FXML private void handleSubmitButtonightShift() {
        updateConfigVariable("Notturno", txtNightShift.getText(), lblNightShift);
    }

    @FXML private void handleSubmitButtonFuel() {
        updateConfigVariable("Carburante", txtFuel.getText(), lblFuel);
    }

    @FXML private void handleSubmitButtonContributi() {
        updateConfigVariable("Contributi", txtContributi.getText(), lblContributi);
    }

    @FXML private void handleSubmitButtonDdfl() {
        updateConfigVariable("DDFL", txtDdfl.getText(), lblDdfl);
    }

    @FXML private void handleSubmitButtonAchievementBonus() {
        updateConfigVariable("Achievement Bonus", txtAchievementBonus.getText(), lblAchievementBonus);
    }

    @FXML private void handleSubmitButtonCutAchievementBonus() {
        updateConfigVariable("Cut Achievement Bonus", txtCutAchievementBonus.getText(), lblCutAchievementBonus);
    }

    private void updateConfigVariable(String variableName, String variableValue, Label label) {
        AppConfigVariable variable = appConfigVariableDAO.getAppConfigVariableByName(variableName);
        variable.setVariableValue(Double.parseDouble(variableValue));
        appConfigVariableDAO.updateAppConfigVariable(variable);
        label.setText(variableValue);
    }

    @FXML private void handleBackButton() {
        stage.setScene(mainScene);
    }
}
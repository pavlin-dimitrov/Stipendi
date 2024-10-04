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

    @FXML
    private TextField txtOvertimeWeek;
    @FXML
    private Label lblOvertimeWeek;
    @FXML
    private TextField txtOvertimeWeekend;
    @FXML
    private Label lblOvertimeWeekend;
    @FXML
    private TextField txtNightShift;
    @FXML
    private Label lblNightShift;
    @FXML
    private TextField txtFuel;
    @FXML
    private Label lblFuel;
    @FXML
    private TextField txtContributi;
    @FXML
    private Label lblContributi;
    @FXML
    private TextField txtDdfl;
    @FXML
    private Label lblDdfl;
    @FXML
    private Button submitOvertimeWeek;
    @FXML
    private Button submitOvertimeWeekend;
    @FXML
    private Button submitNightShift;
    @FXML
    private Button submitFuel;
    @FXML
    private Button submitContributi;
    @FXML
    private Button submitDdfl;
    @FXML
    private Button submitAchievementBonus;
    @FXML
    private Button submitCutAchievementBonus;
    @FXML
    private Button submitDaysThatSetAchBonusToZero;
    @FXML
    private Label lblAchievementBonus;
    @FXML
    private Label lblCutAchievementBonus;
    @FXML
    private Label lblDaysThatSetAchBonusToZero;
    @FXML
    private TextField txtAchievementBonus;
    @FXML
    private TextField txtCutAchievementBonus;
    @FXML
    private TextField txtDaysThatSetAchBonusToZero;
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

    @FXML
    private void initialize() {
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
        lblDaysThatSetAchBonusToZero.setText(String.valueOf(appConfigVariableDAO.
                getAppConfigVariableValueByName("daysThatSetAchBonusToZero")));
        submitOvertimeWeek.setOnAction(actionEvent -> handleSubmitButtonOvertimeWeek());
        submitOvertimeWeekend.setOnAction(actionEvent -> handleSubmitButtonOvertimeWeekend());
        submitNightShift.setOnAction(actionEvent -> handleSubmitButtonightShift());
        submitFuel.setOnAction(actionEvent -> handleSubmitButtonFuel());
        submitContributi.setOnAction(actionEvent -> handleSubmitButtonContributi());
        submitDdfl.setOnAction(actionEvent -> handleSubmitButtonDdfl());
        submitAchievementBonus.setOnAction(actionEvent -> handleSubmitButtonContributi());
        submitCutAchievementBonus.setOnAction(actionEvent -> handleSubmitButtonCutAchievementBonus());
        submitDaysThatSetAchBonusToZero.setOnAction(actionEvent -> handleSubmitDaysThatSetAchBonusToZero());
    }

    @FXML
    private void handleSubmitButtonOvertimeWeek() {
        updateConfigVariable("overtimeWeek", txtOvertimeWeek.getText(), lblOvertimeWeek);
        txtOvertimeWeek.clear();
    }

    @FXML
    private void handleSubmitButtonOvertimeWeekend() {
        updateConfigVariable("overtimeWeekend", txtOvertimeWeekend.getText(), lblOvertimeWeekend);
        txtOvertimeWeekend.clear();
    }

    @FXML
    private void handleSubmitButtonightShift() {
        updateConfigVariable("nightShift", txtNightShift.getText(), lblNightShift);
        txtNightShift.clear();
    }

    @FXML
    private void handleSubmitButtonFuel() {
        updateConfigVariable("fuel", txtFuel.getText(), lblFuel);
        txtFuel.clear();
    }

    @FXML
    private void handleSubmitButtonContributi() {
        updateConfigVariable("contributi", txtContributi.getText(), lblContributi);
        txtContributi.clear();
    }

    @FXML
    private void handleSubmitButtonDdfl() {
        updateConfigVariable("ddfl", txtDdfl.getText(), lblDdfl);
        txtDdfl.clear();
    }

    @FXML
    private void handleSubmitButtonAchievementBonus() {
        updateConfigVariable("achievementBonus", txtAchievementBonus.getText(), lblAchievementBonus);
        txtAchievementBonus.clear();
    }

    @FXML
    private void handleSubmitButtonCutAchievementBonus() {
        updateConfigVariable("partOfAchievementBonus", txtCutAchievementBonus.getText(), lblCutAchievementBonus);
        txtCutAchievementBonus.clear();
    }

    @FXML
    private void handleSubmitDaysThatSetAchBonusToZero() {
        updateConfigVariable("daysThatSetAchBonusToZero", txtDaysThatSetAchBonusToZero.getText(), lblDaysThatSetAchBonusToZero);
        txtDaysThatSetAchBonusToZero.clear();
    }

    private void updateConfigVariable(String variableName, String variableValue, Label label) {
        AppConfigVariable variable = appConfigVariableDAO.getAppConfigVariableByName(variableName);
        variable.setVariableValue(Double.parseDouble(variableValue));
        appConfigVariableDAO.updateAppConfigVariable(variable);
        label.setText(variableValue);
    }

    @FXML
    private void handleBackButton() {
        stage.setScene(mainScene);
    }
}
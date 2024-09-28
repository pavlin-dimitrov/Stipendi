module com.example.stipendi {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql; // За MySQL
    requires static lombok; // За Lombok (static, защото Lombok се използва само по време на компилация)
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.xmlbeans;
    requires org.apache.commons.collections4;
    requires java.desktop;

    opens com.example.stipendi to javafx.fxml;
    exports com.example.stipendi;

    opens com.example.stipendi.model to javafx.base; // Отвори моделния пакет за JavaFX, ако се използва в FXML
    exports com.example.stipendi.model; // Експортиране на моделния пакет, ако се използва в други модули

    exports com.example.stipendi.controller; // Добавете тази линия

    exports com.example.stipendi.service;
    exports com.example.stipendi.dao;
    exports com.example.stipendi.util;
    exports com.example.stipendi.util.contract;
    exports com.example.stipendi.util.implementation;
    exports com.example.stipendi.excel;

    opens com.example.stipendi.controller to javafx.fxml;
}


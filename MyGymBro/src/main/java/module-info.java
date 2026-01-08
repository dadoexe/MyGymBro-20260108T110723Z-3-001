module com.example.mygymbro {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.mygymbro to javafx.fxml;
    exports com.example.mygymbro;
    exports com.example.mygymbro.dao;
    opens com.example.mygymbro.dao to javafx.fxml;
    exports com.example.mygymbro.model;
    opens com.example.mygymbro.model to javafx.fxml;
    exports com.example.mygymbro.utils;
    opens com.example.mygymbro.utils to javafx.fxml;
    exports com.example.mygymbro.controller;
    opens com.example.mygymbro.controller to javafx.fxml;
    exports com.example.mygymbro.bean;
    opens com.example.mygymbro.bean to javafx.fxml;
}
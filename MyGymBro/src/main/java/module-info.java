// Aggiungi la parola "open" all'inizio.
// Questo dà il permesso a JavaFX e Gson di guardare in TUTTE le tue cartelle.
open module com.example.mygymbro {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires java.net.http;
    requires com.google.gson;

    // EXPORTS: Servono per dire "queste classi sono pubbliche"
    exports com.example.mygymbro;
    exports com.example.mygymbro.controller;
    exports com.example.mygymbro.views;
    exports com.example.mygymbro.bean;
    exports com.example.mygymbro.model;
    exports com.example.mygymbro.dao;
    exports com.example.mygymbro.utils;

    // NOTA: Ho rimosso tutte le righe "opens ... to javafx.fxml"
    // perché avendo scritto "open module" in cima, non servono più!
}
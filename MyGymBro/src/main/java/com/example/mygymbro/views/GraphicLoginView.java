package com.example.mygymbro.views;

import com.example.mygymbro.controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class GraphicLoginView implements LoginView {
    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Label lblError;

    private LoginController listener;;


    @FXML
    public void initialize() {
        // Verifica se il bottone è stato iniettato correttamente
        if (btnLogin == null) {
            System.out.println("ERRORE FXML: 'btnLogin' è NULL! Controlla fx:id nel file FXML.");
        } else {
            // Collega l'azione del click
            btnLogin.setOnAction(event -> handleLogin());
            System.out.println("SETUP OK: Bottone collegato correttamente.");
        }
    }

    // Metodo privato per gestire il click (chiama il listener)
    private void handleLogin() {
        System.out.println("1. CLICK: Il bottone è stato premuto!"); // <--- SPIA 1

        if (listener == null) {
            System.out.println("ERRORE GRAVISSIMO: Il listener è NULL. Il controller non è collegato!");
        } else {
            System.out.println("2. VIEW: Sto chiamando il controller..."); // <--- SPIA 2
            listener.validateLogin(getUsername(), getPassword());
        }
    }

    // --- Implementazione dei metodi dell'interfaccia LoginView (dal Diagramma) ---

    @Override
    public String getUsername() {
        // [cite: 96]
        return txtUsername.getText();
    }

    @Override
    public String getPassword() {
        // [cite: 97]
        return txtPassword.getText();
    }

    @Override
    public void setListener(LoginController listener) {
        // [cite: 98]
        this.listener = listener;
    }

    // --- Implementazione dei metodi ereditati da View (dal Diagramma) ---


    public void showMessage(String message) {
        // [cite: 39] Visualizza il messaggio nella label di errore
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
        }
    }


    public void show() {
        // [cite: 39] In JavaFX la visibilità è gestita dallo Stage in ApplicationController.
        // Questo metodo potrebbe servire per portare la finestra in primo piano o resettare i campi.
        if (lblError != null) lblError.setVisible(false);
        if (txtUsername != null) txtUsername.clear();
        if (txtPassword != null) txtPassword.clear();
    }
}

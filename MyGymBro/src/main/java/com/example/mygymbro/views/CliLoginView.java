package com.example.mygymbro.views;

import com.example.mygymbro.controller.LoginController;
import java.util.Scanner;

public class CliLoginView implements LoginView {

    private LoginController listener;
    private Scanner scanner;

    // Variabili per salvare l'input utente
    private String usernameInput;
    private String passwordInput;

    public CliLoginView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void show() {
        System.out.println("\n=== LOGIN MYGYMBRO (CLI) ===");

        System.out.print("Username: ");
        this.usernameInput = scanner.nextLine();

        System.out.print("Password: ");
        this.passwordInput = scanner.nextLine();

        // Chiamiamo il controller per tentare il login
        if (listener != null) {
            listener.checkLogin();
        }
    }

    @Override
    public void setListener(LoginController listener) {
        this.listener = listener;
    }

    @Override
    public String getUsername() {
        return this.usernameInput;
    }

    @Override
    public String getPassword() {
        return this.passwordInput;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(">> INFO: " + message);
    }

    // Se l'interfaccia LoginView ha il metodo close(), implementalo vuoto (la CLI non si chiude)
    @Override
    public void close() { }

    @Override
    public void showMessage() {

    }
}
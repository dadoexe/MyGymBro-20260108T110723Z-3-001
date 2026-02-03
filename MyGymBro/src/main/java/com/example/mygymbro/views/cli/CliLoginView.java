package com.example.mygymbro.views.cli;

import com.example.mygymbro.controller.LoginController;
import com.example.mygymbro.views.LoginView;

import java.util.Scanner;

public class CliLoginView implements LoginView, CliView {

    private LoginController listener;
    private final Scanner scanner;

    // Variabili per memorizzare l'input
    private String username;
    private String password;

    public CliLoginView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("===============================");
        System.out.println("   BENVENUTO IN MYGYMBRO CLI   ");
        System.out.println("===============================");

        // IL TRUCCO È QUI: while(true) impedisce al programma di chiudersi
        while (true) {
            System.out.print("\nInserisci Username (o 'exit' per uscire): ");
            this.username = scanner.nextLine();

            // Uscita di sicurezza
            if (this.username.equalsIgnoreCase("exit")) {
                System.out.println("Arrivederci!");
                System.exit(0);
            }

            System.out.print("Inserisci Password: ");
            this.password = scanner.nextLine();

            // Chiamiamo il controller per verificare
            if (listener != null) {
                // Se il login ha SUCCESSO:
                // Il controller chiamerà loadHome() -> che chiamerà la run() della Dashboard.
                // Il flusso di codice "entrerà" nella dashboard e questo while resterà in pausa.

                // Se il login FALLISCE:
                // Il controller chiamerà showError(), poi il metodo checkLogin() finirà
                // e torneremo qui, pronti per il prossimo giro del while!
                listener.checkLogin();
            }
        }
    }

    // --- IMPLEMENTAZIONE METODI INTERFACCIA ---

    @Override
    public void setListener(LoginController listener) {
        this.listener = listener;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void showSuccess(String message) {
        System.out.println(">> LOGIN OK: " + message);
    }

    @Override
    public void showError(String message) {
        System.out.println("!! ERRORE: " + message + " Riprova.");
    }
}
package com.example.mygymbro;

import com.example.mygymbro.controller.ApplicationController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {
        // --- PUNTO DI INGRESSO JAVAFX (GRAFICA) ---

        ApplicationController app = ApplicationController.getInstance();

        // Configuriamo l'app in modalità GRAFICA (True) passando lo Stage
        app.configure(true, primaryStage);

        // Avviamo la navigazione
        app.start();
    }

    public static void main(String[] args) {
        // Controlliamo se l'utente vuole la modalità CLI (Console)
        // Puoi testarlo scrivendo "--cli" negli argomenti di avvio di IntelliJ
        boolean wantCli = false;

        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            wantCli = true;
        }

        // --- INTERRUTTORE DI AVVIO ---
        if (wantCli) {
            // 1. MODALITÀ TESTUALE (CLI)
            System.out.println(">>> AVVIO MYGYMBRO IN MODALITÀ CONSOLE (CLI) <<<");

            ApplicationController app = ApplicationController.getInstance();

            // Configuriamo in modalità CLI (False) e Stage null
            app.configure(false, null);

            // Avviamo (qui partirà il loop della console e il programma si bloccherà qui finché non esci)
            app.start();

        } else {
            // 2. MODALITÀ GRAFICA (DEFAULT)
            // Lancia JavaFX, che chiamerà il metodo start(Stage) qui sopra
            launch(args);
        }
    }
}
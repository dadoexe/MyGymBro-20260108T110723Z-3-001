package com.example.mygymbro.controller;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.views.LoginView;
import com.example.mygymbro.views.AthleteView;
import com.example.mygymbro.views.WorkoutBuilderView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public final class ApplicationController implements Controller {//singleton

    //static variable reference of istance
    private static ApplicationController instance = null;
    boolean isGraphicMode = true; // O false se lanci da terminale
    //private contructor restricted to this class
    private ApplicationController() {
    }

    public static synchronized ApplicationController getInstance() {
        if (instance == null) {
            instance = new ApplicationController();
        }
        return instance;

    }

    // --- 2. GESTIONE DELLO STAGE E DEL CONTROLLER ATTUALE ---
    private Stage mainStage;
    private Controller currentController; // L'interfaccia generica che abbiamo creato


    public void start(Stage primaryStage) {
        this.mainStage = primaryStage;
        //carichiamo la prima schermata
        loadLogin();
        this.mainStage.show();
    }

    //METODI DI NAVIGAZIONE
    public void loadLogin() {
        try {
            if (currentController != null) {
                currentController.dispose();
            }

            LoginView view = null;

            if (isGraphicMode) {
                // --- MODALITÀ GRAFICA (JavaFX) ---
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/schermataLogin.fxml"));
                Parent root = loader.load();
                view = loader.getController(); // Restituisce GraphicLoginView (che implementa LoginView)

                // Impostiamo la scena
                mainStage.setTitle("MyGymBro - Login");
                mainStage.setScene(new Scene(root));
                mainStage.show();
            } /*else {
                // --- MODALITÀ CLI (Console) ---
                view = new CliLoginView(); // Restituisce CliLoginView (che implementa LoginView)
                // (Opzionale) Se la CLI ha bisogno di pulire lo schermo o stampare un header iniziale
                // ((CliLoginView) view).init();
            }*/

            // --- COMUNE A ENTRAMBI ---
            // Il LoginController accetta l'interfaccia 'LoginView', quindi funziona con entrambe
            LoginController controller = new LoginController(view);
            view.setListener(controller);

            this.currentController = controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadHome() {
        // Ipotizziamo di avere un flag impostato all'avvio dell'app


        AthleteView view = null;

        if (isGraphicMode) {
            // --- STRADA GUI (Quella che hai ora) ---
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/mainpage.fxml"));
                Parent root = loader.load();
                view = loader.getController(); // Torna GraphicAthleteView

                mainStage.setScene(new Scene(root));
                mainStage.show();
            } catch (IOException e) { e.printStackTrace(); }

        }/* else {
            // --- STRADA CLI (Nuova) ---
            // Qui NON usi FXMLLoader, istanzi semplicemente la classe
            view = new CliAthleteView();
            // Nota: La CLI non ha uno "Stage" o una "Scene", vive nella console
            ((CliAthleteView) view).start(); // Metodo ipotetico per avviare il loop della CLI
        }*/

        // --- DA QUI IN POI È UGUALE PER TUTTI ---
        // Il Controller non sa se 'view' è Graphic o Cli, e non gliene frega niente!
        NavigationController controller = new NavigationController(view);
        assert view != null;
        view.setListener(controller);
        controller.loadDashboardData(); // Riempie la view (qualunque essa sia)
    }


    // --- VERSIONE 1: CREAZIONE NUOVA SCHEDA (Nessun argomento) ---
    public void loadWorkoutBuilder() {
        // Chiama il metodo principale passando null, perché non c'è nessuna scheda da modificare
        loadWorkoutBuilder(null);
    }

    // --- VERSIONE 2: MODIFICA O CREAZIONE (Logica Principale) ---
    public void loadWorkoutBuilder(WorkoutPlanBean planToEdit) {
        try {
            if (currentController != null) {
                currentController.dispose();
            }

            WorkoutBuilderView view = null;

            if (isGraphicMode) {
                // --- MODALITÀ GRAFICA (GUI) ---
                // Nota: Controlla bene che il path sia corretto! (una sola cartella 'view' o due?)
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/workout_builder.fxml"));
                Parent root = loader.load();
                view = loader.getController(); // Restituisce GraphicWorkoutBuilderView

                // Cambiamo dinamicamente il titolo della finestra
                String title = (planToEdit == null) ? "MyGymBro - Nuova Scheda" : "MyGymBro - Modifica Scheda";
                mainStage.setTitle(title);
                mainStage.setScene(new Scene(root));
                mainStage.show();

            } else {
                // --- MODALITÀ CLI (Command Line) ---
                // Qui useresti la Factory o il costruttore diretto della CLI
                // view = new CliWorkoutBuilderView();
            }

            // --- GESTIONE DEL CONTROLLER (Logica Comune) ---
            PlanManagerController controller;

            if (planToEdit == null) {
                // CASO A: NUOVA SCHEDA -> Usiamo il costruttore base
                controller = new PlanManagerController(view);
            } else {
                // CASO B: MODIFICA -> Usiamo il costruttore che accetta il Bean
                // Questo riempirà automaticamente i campi della vista (GUI o CLI) con i dati vecchi
                controller = new PlanManagerController(view, planToEdit);
            }

            view.setListener(controller);
            this.currentController = controller;

            // Se fossimo in CLI, qui potremmo dover avviare il loop di input:
            // if (!isGraphicMode) ((CliWorkoutBuilderView)view).start();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento del WorkoutBuilder: " + e.getMessage());
        }
    }


    public void logout() {
        // Pulisco la sessione
        SessionManager.getInstance().logout();
        // Torno al login
        loadLogin();
    }

    @Override
    public void dispose() {
        // 1. Chiudo il controller della schermata attuale (se esiste)
        // Questo è fondamentale se quel controller ha connessioni aperte o thread attivi!
        if (currentController != null) {
            currentController.dispose();
        }

        // 2. Pulisco la sessione (Logout forzato)
        SessionManager.getInstance().logout();

        // 3. Chiudo la finestra principale (se è ancora aperta)
        if (mainStage != null) {
            mainStage.close();
        }

        System.exit(0);
    }
}
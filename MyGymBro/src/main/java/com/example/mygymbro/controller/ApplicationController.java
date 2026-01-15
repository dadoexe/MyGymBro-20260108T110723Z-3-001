package com.example.mygymbro.controller;

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


    public void loadWorkoutBuilder() {
        try {
            if (currentController != null) {
                currentController.dispose();
            }

            WorkoutBuilderView view = null;

            if (isGraphicMode) {
                // --- MODALITÀ GRAFICA ---
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/workout_builder.fxml"));
                Parent root = loader.load();
                view = loader.getController(); // Restituisce GraphicWorkoutBuilderView

                mainStage.setTitle("MyGymBro - WorkoutBuilder");
                mainStage.setScene(new Scene(root));
                mainStage.show();
            } /*else {
                // --- MODALITÀ CLI ---
                view = new CliWorkoutBuilderView(); // La tua classe che gestisce System.out/in per creare schede
            }*/

            // --- COMUNE ---
            // PlanManagerController gestisce la logica indipendentemente dalla vista
            PlanManagerController controller = new PlanManagerController(view);
            view.setListener(controller);

            this.currentController = controller;

        } catch (Exception e) {
            e.printStackTrace();
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
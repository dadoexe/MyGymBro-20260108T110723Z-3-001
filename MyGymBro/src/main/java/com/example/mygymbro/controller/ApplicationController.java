package com.example.mygymbro.controller;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.views.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import com.example.mygymbro.views.GraphicView;

public final class ApplicationController implements Controller {//singleton

    //static variable reference of istance
    private static ApplicationController instance = null;
    boolean isGraphicMode; // O false se lanci da terminale

    private ViewFactory viewFactory; // LA NOSTRA NUOVA FACTORY!
    private Stage mainStage;         // Usato SOLO in modalità grafica

    //private contructor restricted to this class
    private ApplicationController() {
    }

    public static synchronized ApplicationController getInstance() {
        if (instance == null) {
            instance = new ApplicationController();
        }
        return instance;

    }

    // --- 2. GESTIONE DEL CONTROLLER ATTUALE ---
    private Controller currentController; // L'interfaccia generica che abbiamo creato

    public void configure(boolean isGraphic, Stage stage) {
        this.isGraphicMode = isGraphic;
        this.mainStage = stage; // Sarà null se siamo in CLI, ma va bene!

        // Inizializza la Factory corretta
        if (isGraphic) {
            this.viewFactory = new com.example.mygymbro.views.GraphicViewFactory();
        } else {
            this.viewFactory = new com.example.mygymbro.views.CliViewFactory();
        }
    }

    // Avvio dell'applicazione
    public void start() {
        loadLogin(); // Carica la prima schermata
    }

    //METODI DI NAVIGAZIONE
    public void loadLogin() {
        if (currentController != null) currentController.dispose();

        // 1. CHIEDIAMO ALLA FACTORY (Polimorfismo)
        // Se siamo in GUI, ci dà GraphicLoginView. Se siamo in CLI, ci dà CliLoginView.
        LoginView view = viewFactory.createLoginView();

        // 2. SETUP CONTROLLER (Identico per entrambi)
        LoginController controller = new LoginController(view);
        view.setListener(controller);
        this.currentController = controller;

        // 3. MOSTRARE LA VISTA (Qui dobbiamo gestire la differenza di "contenitore")
        renderView(view);
    }

    private void renderView(Object viewObject) {
        if (isGraphicMode) {
            // --- LOGICA JAVAFX ---
            // Se è una vista grafica, recuperiamo il nodo radice e lo mostriamo
            if (viewObject instanceof GraphicView) {
                Parent root = ((GraphicView) viewObject).getRoot();

                if (root != null) {
                    Scene scene = new Scene(root);
                    // Opzionale: mainStage.setTitle(...) se vuoi gestirlo qui
                    mainStage.setScene(scene);
                    mainStage.show();
                } else {
                    System.err.println("Errore: La vista grafica non ha un contenuto (root è null). Controlla GraphicViewFactory.");
                }
            }
        } else {
            // --- LOGICA CLI ---
            // Se è una vista testuale, chiamiamo semplicemente show()
            // Usiamo l'interfaccia base 'View' invece di 'CliView'
            if (viewObject instanceof View) {
                ((View) viewObject).show();
            }
        }
    }


    public void loadHome() {
        // 1. Pulizia del controller precedente
        if (currentController != null) {
            currentController.dispose();
        }

        // 2. CREAZIONE VISTA TRAMITE FACTORY (Il pezzo che mancava!)
        // La factory deciderà se creare GraphicAthleteView o CliAthleteView
        AthleteView view = viewFactory.createAthleteView();

        // Controllo di sicurezza
        if (view == null) {
            System.err.println("ERRORE CRITICO: La factory ha restituito una view NULL per loadHome!");
            return;
        }

        // 3. Setup del Controller
        NavigationController controller = new NavigationController(view);
        view.setListener(controller);
        this.currentController = controller;

        // 4. Caricamento dati iniziali (Dashboard)
        controller.loadDashboardData();

        // 5. Mostra a video (Render)
        renderView(view);
    }


    // --- VERSIONE 1: CREAZIONE NUOVA SCHEDA (Nessun argomento) ---
    public void loadWorkoutBuilder() {
        // Chiama il metodo principale passando null, perché non c'è nessuna scheda da modificare
        loadWorkoutBuilder(null);
    }

    // --- VERSIONE 2: MODIFICA O CREAZIONE (Logica Principale) ---
    public void loadWorkoutBuilder(WorkoutPlanBean planToEdit) {
        try {
            // 1. Pulizia
            if (currentController != null) {
                currentController.dispose();
            }

            // 2. CREAZIONE VISTA TRAMITE FACTORY (Il fix è qui!)
            WorkoutBuilderView view = viewFactory.createWorkoutBuilderView();

            if (view == null) {
                System.err.println("ERRORE CRITICO: La factory ha restituito NULL per WorkoutBuilderView!");
                return;
            }

            // 3. Setup Controller
            PlanManagerController controller;
            if (planToEdit == null) {
                // CASO A: NUOVA SCHEDA
                controller = new PlanManagerController(view);
            } else {
                // CASO B: MODIFICA SCHEDA
                controller = new PlanManagerController(view, planToEdit);
            }

            view.setListener(controller);
            this.currentController = controller;

            // 4. Mostra a video (Render)
            renderView(view);

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
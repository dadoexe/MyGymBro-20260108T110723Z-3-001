package com.example.mygymbro.views;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.NavigationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class GraphicAthleteView implements AthleteView {

    @FXML private Label lblWelcome;      // Label di benvenuto
    @FXML private Label lblInfo;         // Label per messaggi (es. "Nessuna scheda")
    @FXML private ListView<String> listWorkouts; // La lista grafica delle schede

    private NavigationController listener;

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Utile per pulire l'interfaccia all'avvio.
     */
    @FXML
    public void initialize() {
        if (listWorkouts != null) {
            listWorkouts.getItems().clear();
        }
        if (lblInfo != null) {
            lblInfo.setVisible(false);
        }
    }

    @Override
    public void setListener(NavigationController listener) {
        this.listener = listener;
    }

    // --- GESTIONE BOTTONI FXML ---

    /**
     * Collegato al bottone "Nuova Scheda" nel file mainpage.fxml
     * onAction="#handleCreatePlan"
     */
    @FXML
    public void handleCreatePlan(ActionEvent actionEvent) {
        System.out.println("CLICK RICEVUTO! Listener è: " + listener); // <--- AGGIUNGI QUESTO

        if (listener != null) {
            listener.handleCreateNewPlan();
        } else {
            System.err.println("ERRORE: Il listener (NavigationController) è NULLO!");
        }
    }

    /**
     * Collegato al bottone "Logout" (se presente)
     * onAction="#handleLogout"
     */
    @FXML
    public void handleLogout(ActionEvent actionEvent) {
        if (listener != null) {
            listener.handleLogout();
        }

    }

    // --- IMPLEMENTAZIONE METODI DELL'INTERFACCIA ---

    @Override
    public void setWelcomeMessage(String msg) {
        if (lblWelcome != null) {
            lblWelcome.setText(msg);
        }
    }

    @Override
    public void showNoPlansMessage() {
        if (listWorkouts != null) listWorkouts.getItems().clear();

        if (lblInfo != null) {
            lblInfo.setText("Non hai ancora creato schede.\nClicca 'Nuova Scheda' per iniziare!");
            lblInfo.setVisible(true);
        }
    }

    @Override
    public void updateWorkoutList(List<WorkoutPlanBean> workoutPlans) {
        // Spesso updateWorkoutList e showWorkoutPlans fanno la stessa cosa
        // Deleghiamo tutto a showWorkoutPlans per non duplicare codice.
        showWorkoutPlans(workoutPlans);
    }


    public void showMessage(String message) {
        // Usiamo un Alert grafico invece del System.out
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Aggiungi una lista locale per tenere traccia dei dati veri
    private List<WorkoutPlanBean> localCache;

    @Override
    public void showWorkoutPlans(List<WorkoutPlanBean> plans) {
        this.localCache = plans; // Ci salviamo i dati veri in memoria

        if (listWorkouts == null) return;
        listWorkouts.getItems().clear();

        if (plans.isEmpty()) {
            showNoPlansMessage();
            return;
        }

        if (lblInfo != null) lblInfo.setVisible(false);

        for (WorkoutPlanBean plan : plans) {
            String displayString = plan.getName() + " (" + plan.getCreationDate() + ")";
            listWorkouts.getItems().add(displayString);
        }
    }


    @FXML
    public void handleDeletePlan() {
        // 1. Prendo l'elemento selezionato dalla grafica
        int selectedIndex = listWorkouts.getSelectionModel().getSelectedIndex();

        // Controllo se ho una lista locale di bean (localCache) popolata in showWorkoutPlans
        if (selectedIndex >= 0 && localCache != null && selectedIndex < localCache.size()) {
            WorkoutPlanBean selectedPlan = localCache.get(selectedIndex);

            // 2. Chiamo il CONTROLLER (Listener)
            if (listener != null) {
                listener.handleDeletePlan(selectedPlan);
            }
        } else {
            showError("Seleziona una scheda da eliminare!");
        }
    }

    @FXML
    public void handleEditPlan() {
        int selectedIndex = listWorkouts.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0 && localCache != null && selectedIndex < localCache.size()) {
            WorkoutPlanBean selectedPlan = localCache.get(selectedIndex);

            // 2. Chiamo il CONTROLLER
            if (listener != null) {
                listener.handleEditPlan(selectedPlan);
            }
        } else {
            showError("Seleziona una scheda da modificare!");
        }
    }

    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Warning è giallo, meglio per errori utente
        alert.setContentText(msg);
        alert.showAndWait();
    }



    // Metodo richiesto dall'interfaccia View (se presente nella tua gerarchia)
    @Override
    public void showMessage() {
        // Implementazione vuota o di default se non passi argomenti
    }

    @Override
    public void show() {
        // In JavaFX lo show è gestito dallo Stage nel controller principale,
        // ma lo teniamo qui per rispettare l'interfaccia.
    }

    @Override
    public void close() {
        // In JavaFX il close è gestito dallo Stage.
    }
}
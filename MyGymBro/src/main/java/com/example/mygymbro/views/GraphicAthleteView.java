package com.example.mygymbro.views;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.NavigationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class GraphicAthleteView implements AthleteView, GraphicView {

    @FXML private Label lblWelcome;      // Label di benvenuto
    @FXML private Label lblInfo;         // Label per messaggi (es. "Nessuna scheda")
    @FXML private ListView<String> listWorkouts; // La lista grafica delle schede

    private NavigationController listener;
    private Parent root;
    @FXML
    private Label lblTotalPlans;
    @FXML
    private Label lblLastActivity;
    @FXML
    private ListView<WorkoutPlanBean> listWorkoutPlans;
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
            listener.logout();
        }

    }

    // --- IMPLEMENTAZIONE METODI DELL'INTERFACCIA ---

    @Override
    public void updateWelcomeMessage(String msg) {
        if (lblWelcome != null) {
            lblWelcome.setText("Benvenuto " + msg + "!");
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
        // 1. Aggiorna la lista (CON PROTEZIONE NULL)
        if (listWorkoutPlans != null) {
            listWorkoutPlans.getItems().clear();
            listWorkoutPlans.getItems().addAll(workoutPlans);
        }

        // 2. Aggiorna le Statistiche (CON PROTEZIONE NULL)
        if (lblTotalPlans != null) {
            lblTotalPlans.setText(String.valueOf(workoutPlans.size()));
        }

        if (lblLastActivity != null) {
            if (!workoutPlans.isEmpty()) {
                // Prendiamo l'ultima scheda (o la prima, dipende dall'ordinamento)
                WorkoutPlanBean last = workoutPlans.get(workoutPlans.size() - 1);

                // Formattazione data semplice
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                String dateStr = (last.getCreationDate() != null) ? sdf.format(last.getCreationDate()) : "Recente";

                lblLastActivity.setText(dateStr);
            } else {
                lblLastActivity.setText("--");
            }
        }
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
    public void handleDeletePlan(ActionEvent event) {
        // CORREZIONE: Usa 'listWorkoutPlans' invece di 'listWorkouts'
        WorkoutPlanBean selected = listWorkoutPlans.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if (listener != null) {
                listener.deletePlan(selected);
            }
        } else {
            System.out.println("Nessuna scheda selezionata per l'eliminazione.");
        }
    }

    @FXML
    public void handleEditPlan(ActionEvent event) {
        // CORREZIONE: Usa 'listWorkoutPlans' invece di 'listWorkouts'
        WorkoutPlanBean selected = listWorkoutPlans.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if (listener != null) {
                listener.modifyPlan(selected);
            }
        } else {
            // Opzionale: Mostra errore "Seleziona una scheda!"
            System.out.println("Nessuna scheda selezionata.");
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

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public void setRoot(Parent root) {
        this.root = root;
    }
}
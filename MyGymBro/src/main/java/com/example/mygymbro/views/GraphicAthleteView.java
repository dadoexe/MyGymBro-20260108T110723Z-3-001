package com.example.mygymbro.views;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.NavigationController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class GraphicAthleteView implements AthleteView {

    @FXML private Label lblWelcome;     // Collega al file FXML!
    @FXML private Label lblInfo;        // Per messaggi tipo "Nessuna scheda"
    @FXML private ListView<String> listWorkouts; // Per mostrare le schede

    private NavigationController listener;

    @Override
    public void setListener(NavigationController listener) {
        this.listener = listener;
    }

    @Override
    public void updateWorkoutList(List<WorkoutPlanBean> workoutPlans) {

    }

    @Override
    public void setWelcomeMessage(String msg) {
        // Controllo null per sicurezza (se ti dimentichi l'fx:id)
        if (lblWelcome != null) {
            lblWelcome.setText(msg);
        }
    }

    @Override
    public void showWorkoutPlans(List<WorkoutPlanBean> plans) {
        if (listWorkouts == null) return;

        listWorkouts.getItems().clear();
        if (lblInfo != null) lblInfo.setVisible(false); // Nascondi avvisi

        // Popola la lista
        for (WorkoutPlanBean plan : plans) {
            // Aggiungi stringhe alla lista (o oggetti se usi una CellFactory)
            listWorkouts.getItems().add(plan.getName() + " (" + plan.getCreationDate() + ")");
        }
    }

    @Override
    public void showNoPlansMessage() {
        if (listWorkouts != null) listWorkouts.getItems().clear();

        if (lblInfo != null) {
            lblInfo.setText("Non hai ancora creato schede. Clicca 'Nuova Scheda' per iniziare!");
            lblInfo.setVisible(true);
        }
    }

    // Metodi di View (show/showMessage) vanno implementati anche se vuoti per ora
    @Override
    public void show() {}

    @Override
    public void close() {

    }

    @Override
    public void showMessage() {

    }


    public void showMessage(String message) {
        // Magari un Alert o una label di errore
        System.out.println("MSG: " + message);
    }
}
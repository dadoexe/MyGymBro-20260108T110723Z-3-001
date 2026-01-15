package com.example.mygymbro.views;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.controller.PlanManagerController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class GraphicWorkoutBuilderView implements WorkoutBuilderView {

    @FXML private TextField txtPlanName;
    @FXML private TextArea txtComment;
    @FXML private ComboBox<ExerciseBean> comboExercises; // Il menu a tendina
    @FXML private TextField txtSets, txtReps, txtRest;
    @FXML private TableView<WorkoutExerciseBean> tableExercises;

    // Colonne Tabella
    @FXML private TableColumn<WorkoutExerciseBean, String> colName;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colSets;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colReps;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colRest;

    private PlanManagerController listener;

    @FXML
    public void initialize() {
        // Configuriamo le colonne della tabella
        colName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        colSets.setCellValueFactory(new PropertyValueFactory<>("sets"));
        colReps.setCellValueFactory(new PropertyValueFactory<>("reps"));
        colRest.setCellValueFactory(new PropertyValueFactory<>("restTime"));
    }

    @Override
    public void setListener(PlanManagerController listener) {
        this.listener = listener;
    }

    @Override
    public void populateExerciseMenu(List<ExerciseBean> exercises) {
        // Popoliamo il menu a tendina con i dati dell'API
        comboExercises.setItems(FXCollections.observableArrayList(exercises));

        // Questo serve a mostrare solo il nome dell'esercizio nel menu
        comboExercises.setConverter(new StringConverter<>() {
            @Override
            public String toString(ExerciseBean object) {
                return object != null ? object.getName() : "";
            }
            @Override
            public ExerciseBean fromString(String string) {
                return null; // Non ci serve
            }
        });
    }

    // --- AZIONI BOTTONI ---

    @FXML
    public void onAddExercise() {
        // 1. Recuperiamo i dati dai campi
        ExerciseBean selected = comboExercises.getValue();
        if (selected == null) {
            showError("Seleziona un esercizio!");
            return;
        }

        try {
            int sets = Integer.parseInt(txtSets.getText());
            int reps = Integer.parseInt(txtReps.getText());
            int rest = Integer.parseInt(txtRest.getText());

            // 2. Creiamo il Bean della riga
            WorkoutExerciseBean row = new WorkoutExerciseBean();
            row.setExerciseName(selected.getName());
            row.setSets(sets);
            row.setReps(reps);
            row.setRestTime(rest);

            // 3. Aggiungiamo alla tabella grafica
            tableExercises.getItems().add(row);

            // 4. Aggiorniamo anche il Bean nel Controller (Fondamentale!)
            // Nota: Se il PlanManagerController usa la View per leggere i dati alla fine,
            // basta esporre un metodo getExerciseList() come vedi sotto.

        } catch (NumberFormatException e) {
            showError("Inserisci numeri validi per Sets, Reps e Rest.");
        }
    }

    @FXML
    public void onSavePlan() {
        if (listener != null) {
            listener.handleSavePlan();
        }
    }

    @FXML
    public void onCancel() {
        // Torna indietro (logica gestita eventualmente dal controller)
    }

    // --- METODI DELL'INTERFACCIA WorkoutBuilderView ---

    @Override
    public String getPlanName() {
        return txtPlanName.getText();
    }
    public void setPlanName(String planName) {}

    public String getComment() {
        return txtComment.getText();
    }


    public List<WorkoutExerciseBean> getAddedExercises() {
        // Il controller chiamerà questo metodo per sapere cosa salvare
        return tableExercises.getItems();
    }


    public void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }

    @Override
    public void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }
    // --- METODO MANCANTE RICHIESTO DALL'INTERFACCIA ---
    @Override
    public void updateExerciseTable(List<WorkoutExerciseBean> exercises) {
        if (exercises != null) {
            // Converto la lista normale in una lista "osservabile" per JavaFX
            tableExercises.setItems(FXCollections.observableArrayList(exercises));
        } else {
            tableExercises.getItems().clear();
        }
    }

    // Metodi getter/setter opzionali richiesti dall'interfaccia
    @Override public ExerciseBean getSelectedExercise() { return comboExercises.getValue(); }
    @Override public String getSets() { return txtSets.getText(); }
    public String getReps() { return txtReps.getText(); }
   public String getRestTime() { return txtRest.getText(); }
}
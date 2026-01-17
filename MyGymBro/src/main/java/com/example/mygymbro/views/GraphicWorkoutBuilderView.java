package com.example.mygymbro.views;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.controller.PlanManagerController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphicWorkoutBuilderView implements WorkoutBuilderView {

    @FXML private TextField txtPlanName;
    @FXML private TextArea txtComment;
    @FXML private ComboBox<ExerciseBean> comboExercises; // Il menu a tendina
    @FXML private TextField txtSets, txtReps, txtRest;
    @FXML private TableView<WorkoutExerciseBean> tableExercises;
    @FXML private TextField txtSearchExercise;
    private List<ExerciseBean> allExercisesCache = new ArrayList<>();

    // Colonne Tabella
    @FXML private TableColumn<WorkoutExerciseBean, String> colName;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colSets;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colReps;
    @FXML private TableColumn<WorkoutExerciseBean, Integer> colRest;
    @FXML private TableColumn<WorkoutExerciseBean, String> colMuscle;
    @FXML private TableColumn<WorkoutExerciseBean, Void> colDelete;
    private PlanManagerController listener;

    @FXML
    public void initialize() {
        // 1. Rendi la tabella editabile
        tableExercises.setEditable(true);

        // ... configurazione colName e colMuscle (che non sono editabili) ...
        colName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        colMuscle.setCellValueFactory(new PropertyValueFactory<>("muscleGroup"));

        // 2. CONFIGURA SETS (Editabile)
        colSets.setCellValueFactory(new PropertyValueFactory<>("sets"));
        colSets.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSets.setOnEditCommit(event -> {
            WorkoutExerciseBean row = event.getRowValue();
            row.setSets(event.getNewValue());
        });

        // 3. CONFIGURA REPS (Editabile)
        colReps.setCellValueFactory(new PropertyValueFactory<>("reps"));
        colReps.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colReps.setOnEditCommit(event -> {
            WorkoutExerciseBean row = event.getRowValue();
            row.setReps(event.getNewValue());
        });

        // 4. CONFIGURA REST (Editabile)
        colRest.setCellValueFactory(new PropertyValueFactory<>("restTime"));
        colRest.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colRest.setOnEditCommit(event -> {
            WorkoutExerciseBean row = event.getRowValue();
            row.setRestTime(event.getNewValue());
        });

        // 4.B FILTRO RICERCA ESERCIZI
        txtSearchExercise.textProperty().addListener((observable, oldValue, newValue) -> {
            performLiveSearch(newValue);
        });

        // 5. CONFIGURA COLONNA ELIMINA (Bottone X)
        javafx.util.Callback<TableColumn<WorkoutExerciseBean, Void>, TableCell<WorkoutExerciseBean, Void>> cellFactory = new javafx.util.Callback<>() {
            @Override
            public TableCell<WorkoutExerciseBean, Void> call(final TableColumn<WorkoutExerciseBean, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("X");

                    {
                        btn.setStyle("-fx-background-color: #FF5252; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                        btn.setOnAction(event -> {
                            // Logica di eliminazione
                            WorkoutExerciseBean exercise = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(exercise);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colDelete.setCellFactory(cellFactory);
    }

    private void performLiveSearch(String query) {
        if (listener == null) return;

        // Creiamo un Thread separato per scaricare i dati senza bloccare l'interfaccia
        new Thread(() -> {

            // 1. Scarica i dati (lento) in background
            List<ExerciseBean> results = listener.searchExercisesOnApi(query);

            // 2. Aggiorna la grafica (veloce) nel thread principale
            javafx.application.Platform.runLater(() -> {
                comboExercises.setItems(FXCollections.observableArrayList(results));

                if (!results.isEmpty()) {
                    comboExercises.show(); // Apre la tendina
                }
            });

        }).start();
    }

    @Override
    public void setListener(PlanManagerController listener) {
        this.listener = listener;
    }

    @Override
    public void populateExerciseMenu(List<ExerciseBean> exercises) {
        // 1. SALVIAMO I DATI NELLA CACHE (Fondamentale!)
        this.allExercisesCache = exercises;
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
        if (txtSearchExercise != null && !txtSearchExercise.getText().isEmpty()) {
            filterExercises(txtSearchExercise.getText());
        }
    }

    private void filterExercises(String searchText) {
        // Se la ricerca è vuota, mostriamo TUTTO (recuperando dalla cache)
        if (searchText == null || searchText.isEmpty()) {
            comboExercises.setItems(FXCollections.observableArrayList(allExercisesCache));
            return;
        }

        // Altrimenti filtriamo la lista
        List<ExerciseBean> filteredList = allExercisesCache.stream()
                .filter(exercise -> exercise.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        comboExercises.setItems(FXCollections.observableArrayList(filteredList));

        // Opzionale: apre la tendina automaticamente per far vedere i risultati
        if (!filteredList.isEmpty()) {
            comboExercises.show();
        }
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
            row.setMuscleGroup(selected.getMuscleGroup());
            row.setSets(sets);
            row.setReps(reps);
            row.setRestTime(rest);

            // 3. Aggiungiamo alla tabella grafica
            tableExercises.getItems().add(row);

            // 4. Aggiorniamo anche il Bean nel Controller
            // Nota: Se il PlanManagerController usa la View per leggere i dati alla fine,
            // basta esporre un metodo getExerciseList()

        } catch (NumberFormatException e) {
            showError("Inserisci numeri validi per Sets, Reps e Rest.");
        }
    }

    @FXML
    public void onSavePlan() {
        if (listener != null) {
            // Nota: Non passiamo argomenti, il controller legge dalla view
            listener.handleSavePlan();
        }
    }

    @FXML
    public void onCancel() {
        if (listener != null) {
            listener.handleCancel(); // Chiama il metodo che abbiamo appena creato
        }
    }

    // --- METODI DELL'INTERFACCIA WorkoutBuilderView ---

    @Override
    public String getPlanName() {
        return txtPlanName.getText();
    }
    public void setPlanName(String planName) {

        this.txtPlanName.setText(planName);
    }

    public String getComment() {
        return txtComment.getText();
    }
    public void setPlanComment(String comment) {
        this.txtComment.setText(comment);
    }

    public void preloadData(String name, String description, List<WorkoutExerciseBean> exercises) {
        this.txtPlanName.setText(name);
        this.txtComment.setText(description);

        // Convertiamo la lista in ObservableList per la tabella
        if (exercises != null) {
            this.tableExercises.setItems(FXCollections.observableArrayList(exercises));
        }
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

    @Override
    public void show() {

    }

    @Override
    public void close() {

    }

    @Override
    public void showMessage() {

    }
}
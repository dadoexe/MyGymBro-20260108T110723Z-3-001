package com.example.mygymbro.views;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.controller.PlanManagerController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliWorkoutBuilderView implements WorkoutBuilderView {

    private PlanManagerController listener;
    private Scanner scanner;

    // Dati locali per simulare i campi della GUI
    private String planName;
    private String planComment;
    private List<WorkoutExerciseBean> exercisesTable;

    public CliWorkoutBuilderView() {
        this.scanner = new Scanner(System.in);
        this.exercisesTable = new ArrayList<>();
    }

    @Override
    public void show() {
        System.out.println("\n=================================");
        System.out.println("   WORKOUT BUILDER (CLI MODE)   ");
        System.out.println("=================================");

        // 1. Chiediamo subito i dati della scheda
        if (planName == null || planName.isEmpty()) {
            System.out.print("Inserisci Nome Scheda: ");
            this.planName = scanner.nextLine();
            System.out.print("Inserisci Commento: ");
            this.planComment = scanner.nextLine();
        } else {
            System.out.println("Modifica scheda: " + planName);
        }

        // 2. Loop principale
        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Cerca e Aggiungi Esercizio (API)");
            System.out.println("2. Visualizza Tabella Esercizi");
            System.out.println("3. SALVA SCHEDA");
            System.out.println("0. Annulla / Esci");
            System.out.print("Scelta > ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    handleSearchFlow();
                    break;
                case "2":
                    printTable();
                    break;
                case "3":
                    System.out.println("Salvataggio in corso...");
                    listener.handleSavePlan(); // Il controller chiamerà i getter di questa classe
                    running = false;
                    break;
                case "0":
                    listener.handleCancel();
                    running = false;
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void showMessage() {

    }

    private void handleSearchFlow() {
        System.out.print("\nCerca esercizio (es. Bench): ");
        String query = scanner.nextLine();

        // Chiamo il metodo del controller per cercare ONLINE
        List<ExerciseBean> results = listener.searchExercisesOnApi(query);

        if (results.isEmpty()) {
            System.out.println("Nessun esercizio trovato.");
            return;
        }

        // Mostro i risultati
        System.out.println("\nRisultati API:");
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("%d. %s (%s)\n", (i + 1), results.get(i).getName(), results.get(i).getMuscleGroup());
        }

        // Selezione
        System.out.print("Seleziona numero (0 annulla): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine());
            if (idx > 0 && idx <= results.size()) {
                ExerciseBean selected = results.get(idx - 1);
                askDetailsAndAdd(selected);
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
        }
    }

    private void askDetailsAndAdd(ExerciseBean exercise) {
        try {
            System.out.print("Sets: ");
            int sets = Integer.parseInt(scanner.nextLine());
            System.out.print("Reps: ");
            int reps = Integer.parseInt(scanner.nextLine());
            System.out.print("Recupero (sec): ");
            int rest = Integer.parseInt(scanner.nextLine());

            // Creo il bean della riga
            WorkoutExerciseBean wb = new WorkoutExerciseBean();
            wb.setExerciseName(exercise.getName());
            wb.setMuscleGroup(exercise.getMuscleGroup());
            wb.setSets(sets);
            wb.setReps(reps);
            wb.setRestTime(rest);

            this.exercisesTable.add(wb);
            System.out.println(">>> Esercizio aggiunto alla tabella!");
        } catch (Exception e) {
            System.out.println("Errore inserimento numeri.");
        }
    }

    private void printTable() {
        System.out.println("\n--- SCHEDA ATTUALE ---");
        if (exercisesTable.isEmpty()) {
            System.out.println("(Vuota)");
        } else {
            for (WorkoutExerciseBean wb : exercisesTable) {
                System.out.printf("- %s | %dx%d | Rec: %ds\n", wb.getExerciseName(), wb.getSets(), wb.getReps(), wb.getRestTime());
            }
        }
    }

    // --- IMPLEMENTAZIONE INTERFACCIA ---

    @Override
    public void setListener(PlanManagerController listener) {
        this.listener = listener;
    }

    @Override public String getPlanName() { return this.planName; }
    @Override public String getComment() { return this.planComment; }

    @Override
    public void updateTotalTime(String timeMessage) {
        System.out.println("\n[INFO] " + timeMessage);
    }

    @Override
    public List<WorkoutExerciseBean> getAddedExercises() {
        return this.exercisesTable;
    }

    @Override
    public void updateExerciseTable(List<WorkoutExerciseBean> exercises) {
        // Usato quando carichiamo una scheda esistente per modificarla
        this.exercisesTable = exercises;
        printTable();
    }

    public void showMessage(String msg) { System.out.println("[INFO] " + msg); }
    @Override public void showError(String msg) { System.out.println("[ERROR] " + msg); }

    // Metodi grafici che la CLI ignora o gestisce internamente
    @Override public void setPlanName(String planName) { this.planName = planName; }
    @Override public void setPlanComment(String comment) { this.planComment = comment; }
    @Override public void populateExerciseMenu(List<ExerciseBean> exercises) { /* CLI gestisce la ricerca live */ }

    // Getter grafici non usati direttamente dal controller in fase di salvataggio (perché usa getAddedExercises)
    @Override public ExerciseBean getSelectedExercise() { return null; }
    @Override public String getSets() { return "0"; }
    @Override public String getReps() { return "0"; }
     public String getRestTime() { return "0"; }
}
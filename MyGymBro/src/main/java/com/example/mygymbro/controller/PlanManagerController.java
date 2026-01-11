package com.example.mygymbro.controller;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.dao.ExerciseDAO;
import com.example.mygymbro.dao.MySQLExerciseDAO;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.dao.MySQLWorkoutPlanDAO;
import com.example.mygymbro.view.WorkoutBuilderView;

import java.util.List;

public class PlanManagerController implements Controller {

    private WorkoutBuilderView view;
    private WorkoutPlanDAO workoutPlanDAO;
    private ExerciseDAO exerciseDAO;
    private WorkoutPlanBean currentPlan;

    // --- COSTRUTTORE 1: CREAZIONE NUOVO PIANO ---
    public PlanManagerController(WorkoutBuilderView view) {
        this.view = view;

        // 1. Inizializzo i DAO (sempre)
        this.workoutPlanDAO = new MySQLWorkoutPlanDAO();
        this.exerciseDAO = new MySQLExerciseDAO();

        // 2. Creo un Bean VUOTO (perché è un nuovo piano)
        this.currentPlan = new WorkoutPlanBean();

        // 3. Carico la lista degli esercizi disponibili (per il menu a tendina)
        loadAvailableExercises();
    }

    // --- COSTRUTTORE 2: MODIFICA PIANO ESISTENTE ---
    public PlanManagerController(WorkoutBuilderView view, WorkoutPlanBean planToEdit) {
        this.view = view;

        // 1. Inizializzo i DAO
        this.workoutPlanDAO = new MySQLWorkoutPlanDAO();
        this.exerciseDAO = new MySQLExerciseDAO();

        // 2. Uso il Bean PASSATO (perché stiamo modificando)
        this.currentPlan = planToEdit;

        // 3. Popolo la View con i dati vecchi!
        // Qui diciamo alla grafica: "Scrivi 'Scheda Massa' nel titolo e riempi la tabella"
        populateViewWithPlanData();

        // 4. Carico comunque gli esercizi per il menu a tendina
        loadAvailableExercises();
    }

    // --- METODI PRIVATI DI UTILITÀ ---

    private void loadAvailableExercises() {
        // Chiama il DAO per avere la lista di TUTTI gli esercizi del sistema
        // List<ExerciseBean> exercises = exerciseDAO.findAll();
        // view.populateExerciseMenu(exercises);
    }

    private void populateViewWithPlanData() {
        // view.setPlanName(currentPlan.getName());
        // view.updateExerciseTable(currentPlan.getExercises());
    }

    // ... metodi handleAddExercise, handleSavePlan, dispose ...

    @Override
    public void dispose() {
        this.workoutPlanDAO = null;
        this.exerciseDAO = null;
    }
}

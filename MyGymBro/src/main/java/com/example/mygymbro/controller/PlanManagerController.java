package com.example.mygymbro.controller;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.dao.DAOFactory;
import com.example.mygymbro.dao.ExerciseDAO;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.model.*;
import com.example.mygymbro.views.WorkoutBuilderView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PlanManagerController implements Controller {

    private WorkoutBuilderView view;
    private WorkoutPlanDAO workoutPlanDAO;
    private ExerciseDAO exerciseDAO;
    private WorkoutPlanBean currentPlan;
    private UserBean targetAthlete; // Fondamentale per il Trainer

    // --- COSTRUTTORE 1: CREAZIONE NUOVO PIANO ---
    public PlanManagerController(WorkoutBuilderView view) {
        this.view = view;
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
        this.exerciseDAO = DAOFactory.getExerciseDAO(); // Usa la factory!
        this.currentPlan = new WorkoutPlanBean();
        this.currentPlan.setExerciseList(new ArrayList<>()); // Inizializza la lista vuota
        loadAvailableExercises();
    }

    // --- COSTRUTTORE 2: MODIFICA PIANO ESISTENTE ---
    public PlanManagerController(WorkoutBuilderView view, WorkoutPlanBean planToEdit) {
        this.view = view;
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
        this.exerciseDAO = DAOFactory.getExerciseDAO();
        this.currentPlan = planToEdit;
        populateViewWithPlanData();
        loadAvailableExercises();
    }

    public void setTargetAthlete(UserBean athlete) {
        this.targetAthlete = athlete;
    }

    // --- LOGICA DI SALVATAGGIO (UNICA E CORRETTA) ---
    // Questo metodo viene chiamato quando premi "SALVA" nella grafica
    public void handleSavePlan() {
        try {
            // 1. Recupera TUTTI i dati dalla View in un colpo solo
            // (Grazie al metodo che abbiamo appena aggiunto nella View)
            WorkoutPlanBean formData = view.getWorkoutPlanBean();

            // 2. Aggiorna il currentPlan (mantenendo l'ID se esiste)
            currentPlan.setName(formData.getName());
            currentPlan.setComment(formData.getComment());
            currentPlan.setExerciseList(formData.getExerciseList());

            // 3. Validazione
            if (currentPlan.getName() == null || currentPlan.getName().trim().isEmpty()) {
                view.showError("Devi dare un nome alla scheda!");
                return;
            }
            if (currentPlan.getExerciseList() == null || currentPlan.getExerciseList().isEmpty()) {
                view.showError("La scheda deve avere almeno un esercizio.");
                return;
            }

            // 4. Conversione Bean -> Model
            // NOTA: toModelWorkoutPlan gestisce già internamente l'assegnazione dell'ID utente (Trainer o Atleta)
            WorkoutPlan planModel = toModelWorkoutPlan(currentPlan);

            // 5. Salvataggio su DB
            if (currentPlan.getId() > 0) {
                workoutPlanDAO.update(planModel);
            } else {
                workoutPlanDAO.save(planModel);
            }

            view.showSuccess("Scheda salvata correttamente!");

            // 6. Torna alla dashboard corretta
            ApplicationController.getInstance().loadHomeBasedOnRole();

        } catch (Exception e) {
            e.printStackTrace();
            view.showError("Errore durante il salvataggio: " + e.getMessage());
        }
    }

    // --- ANNULLA ---
    public void handleCancel() {
        ApplicationController.getInstance().loadHomeBasedOnRole();
    }

    // Metodo legacy per la CLI (se serve), lo mappiamo su handleSavePlan
    public void submit() {
        handleSavePlan();
    }

    // --- METODI DI SUPPORTO (Exercise, Time, ecc.) ---

    public void addExerciseToPlan(WorkoutExerciseBean newExercise) {
        if (newExercise == null) return;
        if (currentPlan.getExerciseList() == null) currentPlan.setExerciseList(new ArrayList<>());

        currentPlan.getExerciseList().add(newExercise);
        view.updateExerciseTable(currentPlan.getExerciseList());
        calculateDuration();
    }

    public void removeExerciseFromPlan(WorkoutExerciseBean exerciseToRemove) {
        if (currentPlan != null && currentPlan.getExerciseList() != null) {
            currentPlan.getExerciseList().remove(exerciseToRemove);
            view.updateExerciseTable(currentPlan.getExerciseList());
            calculateDuration();
        }
    }

    private void calculateDuration() {
        int totalSeconds = 0;
        if (currentPlan != null && currentPlan.getExerciseList() != null) {
            for (WorkoutExerciseBean ex : currentPlan.getExerciseList()) {
                int activeTime = ex.getSets() * ex.getReps() * 4;
                int restTime = ex.getSets() * ex.getRestTime();
                totalSeconds += activeTime + restTime + 60;
            }
        }
        view.updateTotalTime("Durata stimata: ~" + (totalSeconds / 60) + " min");
    }

    private void loadAvailableExercises() {
        try {
            List<Exercise> exercises = exerciseDAO.findAll();
            List<ExerciseBean> beans = exercises.stream().map(this::toExerciseBean).collect(Collectors.toList());
            view.populateExerciseMenu(beans);
        } catch (Exception e) {
            // view.showError("Errore API"); // Silenzioso per evitare spam all'avvio
        }
    }

    public List<ExerciseBean> searchExercisesOnApi(String keyword) {
        try {
            List<Exercise> results = exerciseDAO.search(keyword);
            return results.stream().map(this::toExerciseBean).collect(Collectors.toList());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private void populateViewWithPlanData() {
        if (currentPlan == null) return;
        view.setPlanName(currentPlan.getName());
        view.setPlanComment(currentPlan.getComment());
        view.updateExerciseTable(currentPlan.getExerciseList());
        calculateDuration();
    }

    // --- MAPPERS (Bean <-> Model) ---

    private ExerciseBean toExerciseBean(Exercise e) {
        ExerciseBean bean = new ExerciseBean();
        bean.setId(String.valueOf(e.getId()));
        bean.setName(e.getName());
        bean.setDescription(e.getDescription());
        bean.setMuscleGroup(e.getMuscleGroup() != null ? e.getMuscleGroup().name() : "UNKNOWN");
        return bean;
    }

    // QUESTO È IL METODO CRUCIALE PER L'ASSEGNAZIONE CORRETTA
    private WorkoutPlan toModelWorkoutPlan(WorkoutPlanBean bean) throws SQLException {
        if (bean == null) return null;

        // --- GESTIONE ID PROPRIETARIO (Fix Trainer/Atleta) ---
        int ownerId;
        if (this.targetAthlete != null) {
            // Se sono un Trainer che crea per un cliente
            ownerId = this.targetAthlete.getId();
        } else {
            // Se sono un utente che crea per me stesso
            UserBean me = SessionManager.getInstance().getCurrentUser();
            if (me == null) throw new IllegalStateException("Nessun utente loggato");
            ownerId = me.getId();
        }

        Athlete athlete = new Athlete();
        athlete.setId(ownerId);

        WorkoutPlan plan = new WorkoutPlan(
                bean.getId(),
                bean.getName(),
                bean.getComment(),
                new Date(),
                athlete
        );

        if (bean.getExerciseList() != null) {
            for (WorkoutExerciseBean web : bean.getExerciseList()) {
                plan.addExercise(toModelWorkoutExercise(web));
            }
        }
        return plan;
    }

    private WorkoutExercise toModelWorkoutExercise(WorkoutExerciseBean b) {
        MuscleGroup mg = MuscleGroup.CHEST; // Default
        try { if(b.getMuscleGroup()!=null) mg = MuscleGroup.valueOf(b.getMuscleGroup()); } catch(Exception e){}

        Exercise definition = new Exercise(0, b.getExerciseName(), "Custom", mg);
        return new WorkoutExercise(definition, b.getSets(), b.getReps(), b.getRestTime());
    }

    @Override
    public void dispose() {
        this.workoutPlanDAO = null;
        this.exerciseDAO = null;
    }
}

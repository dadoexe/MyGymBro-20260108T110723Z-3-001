package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.dao.DAOFactory;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.WorkoutPlan;
import com.example.mygymbro.views.AthleteView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NavigationController implements Controller {

    private AthleteView view;
    private WorkoutPlanDAO workoutPlanDAO;
    // currentUser lo recuperiamo dinamicamente, non serve salvarlo come attributo fisso
    // per evitare che rimanga vecchio se l'utente cambia (caso raro, ma safer).

    public NavigationController(AthleteView view) {
        this.view = view;
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
    }

    public void loadDashboardData() {
        UserBean currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            logout(); // Se la sessione è scaduta, via al login
            return;
        }

        view.updateWelcomeMessage(currentUser.getUsername());

        try {
            // Preparazione dati per il DAO
            Athlete currentAthleteModel = new Athlete();
            currentAthleteModel.setId(currentUser.getId());
            currentAthleteModel.setUsername(currentUser.getUsername());

            // Chiamata al DAO
            List<WorkoutPlan> plans = workoutPlanDAO.findByAthlete(currentAthleteModel);

            // Conversione
            List<WorkoutPlanBean> planBeans = convertModelsToBeans(plans);

            // Aggiornamento View
            view.updateWorkoutList(planBeans);

        } catch (Exception e) {
            e.printStackTrace();
            view.updateWorkoutList(new ArrayList<>());
        }
    }

    // --- METODI DI AZIONE (Standardizzati per CLI e GUI) ---
    public void handleCreateNewPlan() {
        ApplicationController.getInstance().loadWorkoutBuilder();
    }

    // 1. Modifica Scheda
    public void modifyPlan(WorkoutPlanBean plan) {
        // Carichiamo il Builder passando la scheda da modificare
        ApplicationController.getInstance().loadWorkoutBuilder(plan);
    }

    // 2. Elimina Scheda
    // Rinominato da 'handleDeletePlan' a 'deletePlan' per coerenza con la View CLI
    public void deletePlan(WorkoutPlanBean planBean) {
        try {
            // Nota: Assicurati che il tuo DAO abbia il metodo deleteById o delete(int)
            // Se nel DAO si chiama deletePlan, cambia qui sotto.
            workoutPlanDAO.delete(planBean.getId());

            System.out.println(">> Controller: Scheda eliminata (" + planBean.getName() + ")");

            // Ricarichiamo i dati aggiornati
            loadDashboardData();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore cancellazione: " + e.getMessage());
        }
    }

    // --- NAVIGAZIONE ---

    public void loadWorkoutBuilder() {
        // Nuova scheda vuota
        ApplicationController.getInstance().loadWorkoutBuilder();
    }

    public void logout() {
        // Unica fonte di verità per il logout
        ApplicationController.getInstance().logout();
    }

    @Override
    public void dispose() {
        this.workoutPlanDAO = null;
    }

    // --- HELPER DI CONVERSIONE ---
    private List<WorkoutPlanBean> convertModelsToBeans(List<WorkoutPlan> models) {
        List<WorkoutPlanBean> beans = new ArrayList<>();
        if (models == null) return beans;

        for (WorkoutPlan plan : models) {
            WorkoutPlanBean bean = new WorkoutPlanBean();
            bean.setId(plan.getId()); // Fondamentale per l'eliminazione/modifica
            bean.setName(plan.getName());
            bean.setComment(plan.getComment());
            bean.setCreationDate(plan.getCreationDate());

            List<WorkoutExerciseBean> exerciseBeans = new ArrayList<>();
            if (plan.getExercises() != null) {
                for (com.example.mygymbro.model.WorkoutExercise modelEx : plan.getExercises()) {
                    WorkoutExerciseBean exBean = new WorkoutExerciseBean();

                    // Logica difensiva per il nome esercizio
                    if (modelEx.getExercise() != null) {
                        exBean.setExerciseName(modelEx.getExercise().getName());
                    } else if (modelEx.getExerciseDefinition() != null) {
                        exBean.setExerciseName(modelEx.getExerciseDefinition().getName());
                    } else {
                        exBean.setExerciseName("Esercizio");
                    }

                    // Logica difensiva per il gruppo muscolare
                    String muscle = "Misto";
                    if (modelEx.getExercise() != null && modelEx.getExercise().getMuscleGroup() != null) {
                        muscle = modelEx.getExercise().getMuscleGroup().name();
                    } else if (modelEx.getExerciseDefinition() != null && modelEx.getExerciseDefinition().getMuscleGroup() != null) {
                        muscle = modelEx.getExerciseDefinition().getMuscleGroup().name();
                    }

                    exBean.setMuscleGroup(muscle);
                    exBean.setSets(modelEx.getSets());
                    exBean.setReps(modelEx.getReps());
                    exBean.setRestTime(modelEx.getRestTime());
                    exerciseBeans.add(exBean);
                }
            }
            bean.setExerciseList(exerciseBeans);
            beans.add(bean);
        }
        return beans;
    }
}
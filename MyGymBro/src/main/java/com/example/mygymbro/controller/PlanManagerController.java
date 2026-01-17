package com.example.mygymbro.controller;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.dao.DAOFactory; // <--- IMPORTANTE
import com.example.mygymbro.dao.ExerciseDAO;
import com.example.mygymbro.dao.RestApiExerciseDAO;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.model.*;
import com.example.mygymbro.views.WorkoutBuilderView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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


    // --- COSTRUTTORE 1: CREAZIONE NUOVO PIANO ---
    public PlanManagerController(WorkoutBuilderView view) {
        this.view = view;

        // 1. USARE LA FACTORY! (Altrimenti addio Demo Mode)
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
        //this.exerciseDAO = DAOFactory.getExerciseDAO();
        this.exerciseDAO = new com.example.mygymbro.dao.RestApiExerciseDAO();

        // 2. Creo un Bean VUOTO
        this.currentPlan = new WorkoutPlanBean();

        // 3. Carico esercizi
        loadAvailableExercises();
    }

    // --- COSTRUTTORE 2: MODIFICA PIANO ESISTENTE ---
    public PlanManagerController(WorkoutBuilderView view, WorkoutPlanBean planToEdit) {
        this.view = view;

        // 1. USARE LA FACTORY!
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
        //this.exerciseDAO = DAOFactory.getExerciseDAO();
        this.exerciseDAO = new com.example.mygymbro.dao.RestApiExerciseDAO();

        // 2. Uso il Bean passato
        this.currentPlan = planToEdit;

        // 3. Popolo la View
        populateViewWithPlanData();
        loadAvailableExercises();
    }

    // --- METODI DI UTILITÀ ---

    private void loadAvailableExercises() {
        try {
            // Il DAO restituisce MODEL
            List<Exercise> exercises = exerciseDAO.findAll();
            System.out.println("DEBUG: Esercizi scaricati dall'API: " + exercises.size());
            // Convertiamo in BEAN per la View
            List<ExerciseBean> beans = exercises.stream()
                    .map(this::toExerciseBean)
                    .collect(Collectors.toList());

            view.populateExerciseMenu(beans);
        } catch (Exception e) {
            // Catch generico perché RestApiExerciseDAO potrebbe lanciare eccezioni non SQL
            view.showError("Impossibile caricare gli esercizi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateViewWithPlanData() {
        if (currentPlan == null) return;
        view.setPlanName(currentPlan.getName());
        view.setPlanComment(currentPlan.getComment());
        view.updateExerciseTable(currentPlan.getExerciseList());
    }



    // --- MAPPING (Traduttori) ---

    // Model -> Bean
    private ExerciseBean toExerciseBean(Exercise e) {
        if (e == null) return null;

        ExerciseBean bean = new ExerciseBean();
        // Setto i campi uno per uno (sicuro al 100%)
        bean.setId(String.valueOf(e.getId())); // Converto int -> String
        bean.setName(e.getName());
        bean.setDescription(e.getDescription());
        // Controllo null sul gruppo muscolare per evitare crash
        if (e.getMuscleGroup() != null) {
            bean.setMuscleGroup(e.getMuscleGroup().name());
        } else {
            bean.setMuscleGroup("UNKNOWN");
        }

        // Se nel Bean hai altri campi obbligatori (es. gifUrl), settali qui o lasciali null/vuoti
        // bean.setGifUrl("");

        return bean;
    }

    // Aggiungi questo metodo
    public List<ExerciseBean> searchExercises(String keyword) {
        try {
            // Chiama il nuovo metodo del DAO
            List<Exercise> results = exerciseDAO.search(keyword);

            // Converte in Bean
            return results.stream()
                    .map(this::toExerciseBean)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Bean -> Model (Per WorkoutExercise) - VERSIONE SICURA
    private WorkoutExercise toModelWorkoutExercise(WorkoutExerciseBean b) {
        if (b == null) return null;

        // Recuperiamo il gruppo muscolare dal Bean, o mettiamo CHEST se manca
        MuscleGroup mg = MuscleGroup.CHEST;
        try {
            if (b.getMuscleGroup() != null) {
                mg = MuscleGroup.valueOf(b.getMuscleGroup());
            }
        } catch (Exception e) {
            // Ignora errore di conversione
        }

        // CREIAMO UN NUOVO OGGETTO AL VOLO (Senza cercarlo nel DB)
        // ID 0 dice al DAO: "Sono nuovo, salvami tu se non esisto"
        Exercise definition = new Exercise(
                0,
                b.getExerciseName(),
                "Importato da API",
                mg
        );

        return new WorkoutExercise(definition, b.getSets(), b.getReps(), b.getRestTime());
    }

    // Bean -> Model (Per WorkoutPlan)
    private WorkoutPlan toModelWorkoutPlan(WorkoutPlanBean bean) throws SQLException {
        if (bean == null) return null;

        UserBean userBean = SessionManager.getInstance().getCurrentUser();
        if (userBean == null) {
            throw new IllegalStateException("Errore: Nessun utente loggato.");
        }

        // Creiamo un oggetto "dummy" Athlete solo per passare l'ID al DAO
        // Assumiamo che UserBean abbia un metodo getId() che ritorna l'int del DB
        Athlete athlete = new Athlete();

        // ATTENZIONE: Assicurati che UserBean abbia il metodo getId()!
        // Se getId() ritorna String, fai Integer.parseInt(userBean.getId())
        athlete.setId(userBean.getId());

        // Creo il Model del piano
        int idToUse = bean.getId();

        WorkoutPlan plan = new WorkoutPlan(
                idToUse, // <--- PRIMA C'ERA SCRITTO 0
                bean.getName(),
                bean.getComment(),
                new Date(),
                athlete
        );

        // Aggiungo gli esercizi convertiti
        for (WorkoutExerciseBean web : bean.getExerciseList()) {
            plan.addExercise(toModelWorkoutExercise(web));
        }

        return plan;
    }

    // --- METODO NUOVO PER LA RICERCA ---
    public List<ExerciseBean> searchExercisesOnApi(String keyword) {
        try {
            // Chiama il metodo search del DAO (che abbiamo creato prima)
            List<Exercise> results = exerciseDAO.search(keyword);

            // Converte i risultati in Bean per la grafica
            return results.stream()
                    .map(this::toExerciseBean)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>(); // Ritorna lista vuota se fallisce
        }
    }



    // --- LOGICA DI SALVATAGGIO ---

    public void handleCancel() {
        // Torna semplicemente alla Home / Dashboard
        ApplicationController.getInstance().loadHome();
    }

    // Modifica il metodo handleSavePlan così:
    public void handleSavePlan() {
        try {
            // 1. Aggiorna i dati base dal form
            currentPlan.setName(view.getPlanName());
            currentPlan.setComment(view.getComment());

            // 2. Recupera gli esercizi dalla tabella della View
            // (Assicurati che GraphicWorkoutBuilderView abbia il metodo getAddedExercises())
            List<WorkoutExerciseBean> exercisesFromTable = view.getAddedExercises();
            currentPlan.setExerciseList(exercisesFromTable);

            // Controllo validazione
            if (currentPlan.getName() == null || currentPlan.getName().trim().isEmpty()) {
                view.showError("Devi dare un nome alla scheda!");
                return;
            }

            // 3. Converto BEAN -> MODEL
            WorkoutPlan planModel = toModelWorkoutPlan(currentPlan);

            // 4. DECISIONE: INSERT o UPDATE?
            if (currentPlan.getId() > 0) {
                // Se l'ID è maggiore di 0, la scheda esiste già -> UPDATE
                // Nota: Assicurati di avere il metodo update nel DAO!
                workoutPlanDAO.update(planModel);
                // Se non hai update(), spesso si usa delete(id) + save(model), ma è rischioso per gli ID.
                // L'ideale è implementare update nel DAO.
            } else {
                // ID è 0 o null -> INSERT
                workoutPlanDAO.save(planModel);
            }

            // 5. Torna alla dashboard
            ApplicationController.getInstance().loadHome();

        } catch (SQLException e) {
            view.showError("Errore Database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            view.showError("Errore: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        this.workoutPlanDAO = null;
        this.exerciseDAO = null;
    }
}

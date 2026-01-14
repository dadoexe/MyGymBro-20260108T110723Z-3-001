package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
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

    private final AthleteView athleteHome;
    private WorkoutPlanDAO workoutPlanDAO;
    private UserBean currentUser;

    public NavigationController(AthleteView athleteHome) {
        this.athleteHome = athleteHome;
        // Istanziamo l'implementazione concreta del DAO
        this.workoutPlanDAO = DAOFactory.getWorkoutPlanDAO();
        // Recuperiamo l'utente dalla sessione
        this.currentUser = SessionManager.getInstance().getCurrentUser();
    }

    public void loadDashboardData() {
        if (currentUser == null) {
            handleLogout();
            return;
        }

        // 1. Impostiamo il messaggio di benvenuto
        athleteHome.setWelcomeMessage("Bentornato, " + currentUser.getUsername() + "!");

        try {
            // 2. Preparazione per il DAO:
            // Il DAO vuole un oggetto 'Athlete', ma noi abbiamo un 'UserBean'.
            // Creiamo un oggetto Athlete temporaneo con l'ID dell'utente loggato.
            // Il DAO usa solo l'ID per la query (WHERE Athlete_id = ?), quindi questo basta.
            Athlete currentAthleteModel = new Athlete();
            currentAthleteModel.setId(currentUser.getId());
            currentAthleteModel.setUsername(currentUser.getUsername());

            // 3. Chiamata al DAO
            List<WorkoutPlan> plans = workoutPlanDAO.findByAthlete(currentAthleteModel);

            // 4. Conversione Model -> Bean
            // La View si aspetta dei Bean, ma il DAO ci ha dato dei Model. Dobbiamo convertirli.
            List<WorkoutPlanBean> planBeans = convertModelsToBeans(plans);

            // 5. Aggiornamento View
            if (!planBeans.isEmpty()) {
                athleteHome.showWorkoutPlans(planBeans);
            } else {
                athleteHome.showNoPlansMessage();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // In un'app reale mostreresti un messaggio di errore all'utente
            System.err.println("Errore nel caricamento delle schede: " + e.getMessage());
        }
    }

    public void handleLogout() {
        SessionManager.getInstance().logout();
        ApplicationController.getInstance().loadLogin();
    }

    public void handleCreateNewPlan() {
        ApplicationController.getInstance().loadWorkoutBuilder();
    }

    @Override
    public void dispose() {
        this.workoutPlanDAO = null;
        this.currentUser = null;
    }

    /**
     * Metodo helper per convertire la lista di Model (dal DB) in lista di Bean (per la View)
     */
    private List<WorkoutPlanBean> convertModelsToBeans(List<WorkoutPlan> models) {
        List<WorkoutPlanBean> beans = new ArrayList<>();
        if (models == null) return beans;

        for (WorkoutPlan plan : models) {
            WorkoutPlanBean bean = new WorkoutPlanBean();
            bean.setId(plan.getId());
            bean.setName(plan.getName());
            bean.setComment(plan.getComment());
            bean.setCreationDate(plan.getCreationDate());
            // Se ti serve passare anche gli esercizi nel bean, dovresti convertirli qui
            // Ma per la lista della dashboard (solo nomi e date) questo di solito basta.
            beans.add(bean);
        }
        return beans;
    }
}
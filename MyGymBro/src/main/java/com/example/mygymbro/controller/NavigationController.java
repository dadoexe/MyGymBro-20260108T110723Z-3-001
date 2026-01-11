package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.view.AthleteView;
import com.example.mygymbro.controller.ApplicationController;

public class NavigationController implements Controller {

    private AthleteView athleteHome;
    private WorkoutPlanDAO workoutPlanDAO;
    private UserBean user;

    public NavigationController(AthleteView athleteHome) {
        this.athleteHome = athleteHome;
    }

    public void loadDashboardData(){

    }

    public void HandleLogout(){
        // Pulisco la sessione
        SessionManager.getInstance().logout();
        // Torno al login
        ApplicationController.getInstance().loadLogin();
    }


    public void handleCreateNewPlan(){

    }

    @Override
    public void dispose(){
        this.workoutPlanDAO = null;
    }
}

package com.example.mygymbro.views;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.NavigationController;

import java.util.List;

public interface AthleteView extends View{

    void setListener(NavigationController controller);
    void updateWorkoutList(List<WorkoutPlanBean> workoutPlans);
    void setWelcomeMessage(String msg);
    void showWorkoutPlans(List<WorkoutPlanBean> plans);
    void showNoPlansMessage();

}

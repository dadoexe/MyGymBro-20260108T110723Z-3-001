package com.example.mygymbro.controller;

import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.dao.ExerciseDAO;
import com.example.mygymbro.dao.WorkoutPlanDAO;
import com.example.mygymbro.view.WorkoutBuilderView;

import javax.swing.text.View;

public class PlanManagerController implements Controller{

    private WorkoutBuilderView workoutBuilderView;
    private WorkoutPlanDAO workoutPlanDAO;
    private ExerciseDAO exerciseDAO;
    private WorkoutPlanBean currentPlan;

    public PlanManagerController(){}


    @Override
    public void dispose() {

    }
}

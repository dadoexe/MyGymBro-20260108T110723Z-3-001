package com.example.mygymbro.views;

import com.example.mygymbro.bean.ExerciseBean;
import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.controller.PlanManagerController;

import java.util.List;

public interface WorkoutBuilderView {

    String getPlanName();

     ExerciseBean getSelectedExercise();

     String getSets();

     String getReps();


     void setListener(PlanManagerController controller);

    void populateExerciseMenu(List<ExerciseBean> exercises);

     void setPlanName(String name);

     void updateExerciseTable(List<WorkoutExerciseBean> exercises);
     void showError(String message);

    String getComment();
}

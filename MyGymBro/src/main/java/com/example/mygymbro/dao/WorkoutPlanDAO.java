package com.example.mygymbro.dao;

import com.example.mygymbro.bean.WorkoutPlanBean;
import java.sql.SQLException;
import java.util.List;

public interface WorkoutPlanDAO {
    // Usiamo il Bean
    void savePlan(WorkoutPlanBean workoutPlan) throws SQLException;

    // Cerchiamo per Username (Stringa), è più comodo che passare l'intero oggetto Athlete
    List<WorkoutPlanBean> loadPlansByUsername(String username) throws SQLException;

    void delete(int id) throws SQLException;
    void update(WorkoutPlan plan) throws SQLException;
}

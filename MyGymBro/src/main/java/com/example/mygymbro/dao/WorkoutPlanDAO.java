package com.example.mygymbro.dao;

import com.example.mygymbro.model.WorkoutPlan;

import java.sql.SQLException;

public interface WorkoutPlanDAO {
    void save(WorkoutPlan workoutPlan) throws SQLException;
    void findByAthlete(WorkoutPlan workoutPlan) throws SQLException;
    void delete(int id) throws SQLException;
}

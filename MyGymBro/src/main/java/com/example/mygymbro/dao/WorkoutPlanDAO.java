package com.example.mygymbro.dao;

import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.WorkoutPlan;

import java.sql.SQLException;
import java.util.List;

public interface WorkoutPlanDAO {
    void save(WorkoutPlan workoutPlan) throws SQLException;
    List<WorkoutPlan> findByAthlete(Athlete athlete) throws SQLException;
    void delete(int id) throws SQLException;
    void update(WorkoutPlan plan) throws SQLException;
}

package com.example.mygymbro.dao;

import com.example.mygymbro.model.WorkoutPlan;
import com.example.mygymbro.model.Athlete;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryWorkoutPlanDAO implements WorkoutPlanDAO {

    // "Database" statico per le schede (model.WorkoutPlan)
    private static List<WorkoutPlan> ramPlans = new ArrayList<>();

    @Override
    public void save(WorkoutPlan plan) throws SQLException {
        // Simulazione salvataggio con gestione semplice degli ID
        if (plan.getId() == 0) {
            int newId = ramPlans.size() + 1;
            plan.setId(newId);
        } else {
            ramPlans.removeIf(p -> p.getId() == plan.getId());
        }
        ramPlans.add(plan);
        System.out.println("[RAM DB] Piano salvato: " + plan.getName());
    }

    @Override
    public List<WorkoutPlan> findByAthlete(Athlete athlete) throws SQLException {
        if (athlete == null) return new ArrayList<>();
        return ramPlans.stream()
                .filter(p -> p.getAthlete() != null && p.getAthlete().getId() == athlete.getId())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(int id) throws SQLException {
        ramPlans.removeIf(p -> p.getId() == id);
    }
}
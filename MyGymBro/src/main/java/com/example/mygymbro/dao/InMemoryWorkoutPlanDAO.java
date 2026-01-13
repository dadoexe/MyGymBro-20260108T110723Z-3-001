package com.example.mygymbro.dao;

import com.example.mygymbro.bean.WorkoutPlanBean;
import java.sql.SQLException; // Importante
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryWorkoutPlanDAO implements WorkoutPlanDAO {

    private static List<WorkoutPlanBean> ramPlans = new ArrayList<>();

    @Override
    public void savePlan(WorkoutPlanBean plan) throws SQLException {
        ramPlans.add(plan);
    }

    @Override
    public List<WorkoutPlanBean> loadPlansByUsername(String username) throws SQLException {
        // Nota: Assumiamo che il Bean abbia il campo athleteUsername
        return ramPlans.stream()
                // .filter(p -> p.getAthleteUsername().equals(username)) // Decommenta se hai il campo
                .collect(Collectors.toList());
    }

    @Override
    public void delete(int id) throws SQLException {
        ramPlans.removeIf(p -> p.getId() == id); // Assumendo che il bean abbia getId()
    }
}
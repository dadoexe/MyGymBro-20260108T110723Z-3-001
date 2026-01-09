package com.example.mygymbro.dao;

import com.example.mygymbro.model.WorkoutPlan;
import com.example.mygymbro.utils.DBConnect;

import java.sql.*;

public class MySQLWorkoutPlanDAO implements WorkoutPlanDAO {

    @Override
    public void save(WorkoutPlan workoutPlan) throws SQLException {

        //scrivo la query
        String planQuery = "INSERT INTO workout_plan (name, comment, creationDate, athlete_id) VALUES (?, ?, ?, ?)";
        //dichiaro le risorse
        Connection conn = null;
        PreparedStatement stmtPlan = null;
        ResultSet generatedKeys = null;
        //PreparedStatement stmtExercise= null;

        try {
            conn = DBConnect.getConnection();
            // DISABILITA L'AUTO-COMMIT per gestire la transazione
            // Se qualcosa va storto mentre salvo gli esercizi, annullo anche la creazione della scheda
            conn.setAutoCommit(false);

            stmtPlan = conn.prepareStatement(planQuery, Statement.RETURN_GENERATED_KEYS);
            stmtPlan.setString(1, workoutPlan.getName());
            stmtPlan.setString(2, workoutPlan.getComment());
            stmtPlan.setDate(3, new java.sql.Date(workoutPlan.getCreationDate().getTime()));
            stmtPlan.setInt(4, workoutPlan.getId());
            stmtPlan.executeUpdate();
            generatedKeys = stmtPlan.getGeneratedKeys();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; //rilancia errore al controller
        } finally {
                try {
                    if (generatedKeys != null) {
                        generatedKeys.close();
                    }
                    if (stmtPlan != null) {
                        stmtPlan.close();
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }
        }
    }

        @Override
        public void findByAthlete (WorkoutPlan workoutPlan) throws SQLException {

        }

        @Override
        public void delete(int id) throws SQLException {

        }
    }
}
package com.example.mygymbro.dao;

import com.example.mygymbro.model.*;
import com.example.mygymbro.utils.DAOUtils;
import com.example.mygymbro.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLWorkoutPlanDAO implements WorkoutPlanDAO {

    @Override
    public void save(WorkoutPlan workoutPlan) throws SQLException {

        //scrivo la query
        String planQuery = "INSERT INTO workout_plan (name, comment, creationDate, athlete_id) VALUES (?, ?, ?, ?)";
        String exerciseQuery = "INSERT INTO workout_exercise (workout_plan_id, exercise_id, sets, reps, rest_time) VALUES (?, ?, ?, ?, ?)";
        //dichiaro le risorse
        Connection conn = null;
        PreparedStatement stmtPlan = null;
        PreparedStatement stmtExercise = null; //risorsa per gli esercizi
        ResultSet generatedKeys = null;


        try {
            conn = DBConnect.getConnection();
            // DISABILITA L'AUTO-COMMIT per gestire la transazione
            // Se qualcosa va storto mentre salvo gli esercizi, annullo anche la creazione della scheda
            conn.setAutoCommit(false);

            stmtPlan = conn.prepareStatement(planQuery, Statement.RETURN_GENERATED_KEYS);
            stmtPlan.setString(1, workoutPlan.getName());
            stmtPlan.setString(2, workoutPlan.getComment());
            stmtPlan.setDate(3, new java.sql.Date(workoutPlan.getCreationDate().getTime()));

            //aggiungo l'id DELL'ATLETA per associarlo al corrispettivo piano
            stmtPlan.setInt(4, workoutPlan.getAthlete().getId());

            int affectedRows = stmtPlan.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Creazione scheda fallita, nessuna riga aggiunta.");
            //recupero id generato
            generatedKeys = stmtPlan.getGeneratedKeys();
            int newPlanId = -1;
            if (generatedKeys.next()) {
                newPlanId = generatedKeys.getInt(1);
                workoutPlan.setId(newPlanId);
            }else{throw new SQLException("Workout Plan creation failed, unable to get any id");}

            //ora salviamo i figli (gli esercizi interni al piano)
            //usiamo l'id appena recuperato (newPlanId)
            stmtExercise = conn.prepareStatement(exerciseQuery);
            for(WorkoutExercise ex : workoutPlan.getExercises()){
                //int localExerciseId = getOrInsertExercise(we.getExerciseDefinition(), conn);
                stmtExercise.setInt(1, newPlanId);
                stmtExercise.setInt(2, ex.getExerciseDefinition().getId());
                stmtExercise.setInt(3,ex.getSets());
                stmtExercise.setInt(4, ex.getReps());
                stmtExercise.setInt(5, ex.getRestTime());
                stmtExercise.executeUpdate();
            }
            conn.commit();

        } catch (SQLException e) {
            //facciamo il roll back in caso qualcosa vada storto
            if(conn != null){
                try{conn.rollback();}catch(SQLException ex){ex.printStackTrace();}
            }throw e; //rilancia al controller per l'alert
        } finally {
                try {
                    if(conn != null){ conn.setAutoCommit(true);}//rimetto autocommit a ture prima di chiudere o restituire la ocnn
                }catch(SQLException e){
                    e.printStackTrace();
                }
                DAOUtils.closeStatement(stmtPlan);
                DAOUtils.closeStatement(stmtExercise);
                DAOUtils.closeResultSet(generatedKeys);

                //infine chiudiamo la connesione
            DAOUtils.closeConnection(conn);
        }
    }


    @Override
        public List<WorkoutPlan> findByAthlete (Athlete athlete) throws SQLException {

        String query = "SELECT * FROM workout_plan WHERE Athlete_id = ? ORDER BY creation_date DESC";

        Connection conn= null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<WorkoutPlan> Plans = new ArrayList<>();

        try{
            conn = DBConnect.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, athlete.getId());
            rs = stmt.executeQuery();
            while(rs.next()){
                int planID = rs.getInt("id");
                String name = rs.getString("name");
                String comment = rs.getString("comment");
                Date date = rs.getDate("creation_date");

                WorkoutPlan plan = new WorkoutPlan(planID, name, comment, date, athlete);
                plan.setId(planID);
                //metodo privato che fa una seconda query per riempire la lista
                List<WorkoutExercise> exercises = loadExercisesForPlan(planID, conn);
                for(WorkoutExercise ex : exercises){
                    plan.addExercise(ex);
                }
                Plans.add(plan);
            }
        }finally{
            DAOUtils.close(conn, stmt,rs);
        }
        return Plans;
        }

    /**
     * Questo metodo carica gli esercizi collegati a una specifica scheda.
     * Nota: Riceve la Connection già aperta dal metodo padre per efficienza
     */
    private List<WorkoutExercise> loadExercisesForPlan(int planId, Connection conn) throws SQLException {
        // Faccio una JOIN per avere subito anche i dettagli dell'esercizio (nome, muscolo, ecc)
        // senza dover fare altre query.
        String query = "SELECT we.*, e.name, e.description, e.muscle_group " +
                "FROM workout_exercise we " +
                "JOIN exercise e ON we.exercise_id = e.id " +
                "WHERE we.workout_plan_id = ?";

        List<WorkoutExercise> exercises = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, planId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                // 1. Ricostruisco l'oggetto 'Exercise' (Definizione dal catalogo)
                // Nota: se hai già un metodo mapRowToExercise in ExerciseDAO potresti duplicare logica,
                // ma qui è più sicuro farlo esplicitamente per via della JOIN.
                Exercise definition = new Exercise(
                        rs.getInt("exercise_id"), // ID dell'esercizio puro
                        rs.getString("name"),
                        rs.getString("description"),
                        MuscleGroup.valueOf(rs.getString("muscle_group").toUpperCase())
                );

                // 2. Ricostruisco l'oggetto 'WorkoutExercise' (La riga della scheda)
                // Qui leggo sets, reps, restTime dalla tabella di mezzo 'workout_exercise'
                WorkoutExercise wExercise = new WorkoutExercise(
                        definition, // Passo l'oggetto definition appena creato
                        rs.getInt("sets"),
                        rs.getInt("reps"),
                        rs.getInt("rest_time")
                );

                exercises.add(wExercise);
            }
        } finally {
            // Chiudo solo stmt e rs, NON la connessione (perché appartiene al metodo chiamante)
            DAOUtils.closeStatement(stmt);
            DAOUtils.closeResultSet(rs);
        }
        return exercises;
    }


        @Override
        public void delete(int planId) throws SQLException {


                Connection conn = null;
                PreparedStatement stmtDeleteExercise = null;
                PreparedStatement stmtDeletePlan = null;
                ResultSet rs = null;
                String deleteExerciseQuery = "DELETE FROM workout_exercise WHERE workout_plan_id = ?";
                String deleteWorkoutPlanQuery = "DELETE FROM workout_plan WHERE id = ?";
                try{
                    conn = DBConnect.getConnection();
                    conn.setAutoCommit(false);
                    //cancellazione figli
                    stmtDeleteExercise = conn.prepareStatement(deleteExerciseQuery);
                    stmtDeleteExercise.setInt(1, planId );

                    //cancellazione padre
                    stmtDeletePlan = conn.prepareStatement(deleteWorkoutPlanQuery);
                    stmtDeletePlan.setInt(1, planId);
                    int rowsAffected = stmtDeleteExercise.executeUpdate();

                    if (rowsAffected == 0) {
                        // Opzionale: Se vuoi sapere se l'ID esisteva o no
                        System.out.println("Nessuna scheda trovata con ID: " + planId);
                    }
                    //confermo
                    conn.commit();

                }catch(SQLException e){
                    if(conn != null){
                        try{conn.rollback();}catch(SQLException ex){ex.printStackTrace();}
                    }throw e;

                }finally{
                    DAOUtils.closeStatement(stmtDeleteExercise);
                    DAOUtils.closeStatement(stmtDeletePlan);
                    DAOUtils.closeConnection(conn);
                }
        }

    }

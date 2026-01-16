package com.example.mygymbro.dao;

import com.example.mygymbro.model.*;
import com.example.mygymbro.utils.DAOUtils;
import com.example.mygymbro.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLWorkoutPlanDAO implements WorkoutPlanDAO {

    @Override
    public void save(WorkoutPlan plan) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtPlan = null;
        PreparedStatement stmtLink = null;
        ResultSet generatedKeys = null;

        // Query differenziate
        String insertSQL = "INSERT INTO workout_plan (name, comment, creation_date, athlete_id) VALUES (?, ?, ?, ?)";
        String updateSQL = "UPDATE workout_plan SET name = ?, comment = ?, creation_date = ? WHERE id = ?";

        String deleteExercisesSQL = "DELETE FROM workout_exercise WHERE workout_plan_id = ?";
        String insertLinkSQL = "INSERT INTO workout_exercise (workout_plan_id, exercise_id, sets, reps, rest_time) VALUES (?, ?, ?, ?, ?)";

        // Se l'ID è > 0, significa che la scheda esiste già -> UPDATE
        boolean isUpdate = (plan.getId() > 0);

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Transazione ON

            // --- A. SALVIAMO O AGGIORNIAMO LA SCHEDA (PADRE) ---
            if (isUpdate) {
                stmtPlan = conn.prepareStatement(updateSQL);
                stmtPlan.setString(1, plan.getName());
                stmtPlan.setString(2, plan.getComment());
                stmtPlan.setDate(3, new java.sql.Date(plan.getCreationDate().getTime()));
                stmtPlan.setInt(4, plan.getId()); // WHERE id = ?
                stmtPlan.executeUpdate();
            } else {
                stmtPlan = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                stmtPlan.setString(1, plan.getName());
                stmtPlan.setString(2, plan.getComment());
                stmtPlan.setDate(3, new java.sql.Date(plan.getCreationDate().getTime()));
                stmtPlan.setInt(4, plan.getAthleteId());
                stmtPlan.executeUpdate();

                // Recuperiamo il nuovo ID generato
                generatedKeys = stmtPlan.getGeneratedKeys();
                if (generatedKeys.next()) {
                    plan.setId(generatedKeys.getInt(1));
                }
            }

            // --- B. GESTIONE ESERCIZI (FIGLI) ---
            // Strategia: Se è un update, prima puliamo i vecchi collegamenti
            if (isUpdate) {
                try (PreparedStatement delStmt = conn.prepareStatement(deleteExercisesSQL)) {
                    delStmt.setInt(1, plan.getId());
                    delStmt.executeUpdate();
                }
            }

            // Ora inseriamo gli esercizi (nuovi o aggiornati)
            stmtLink = conn.prepareStatement(insertLinkSQL);
            for (WorkoutExercise we : plan.getExercises()) {
                // 1. Assicuriamoci che l'esercizio esista nel catalogo locale
                int localExerciseId = getOrInsertExercise(we.getExerciseDefinition(), conn);

                // 2. Creiamo il collegamento
                stmtLink.setInt(1, plan.getId());
                stmtLink.setInt(2, localExerciseId);
                stmtLink.setInt(3, we.getSets());
                stmtLink.setInt(4, we.getReps());
                stmtLink.setInt(5, we.getRestTime());
                stmtLink.addBatch();
            }
            stmtLink.executeBatch();

            conn.commit(); // Conferma tutto

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (generatedKeys != null) DAOUtils.closeResultSet(generatedKeys);
            if (stmtPlan != null) DAOUtils.closeStatement(stmtPlan);
            if (stmtLink != null) DAOUtils.closeStatement(stmtLink);
            if (conn != null) DAOUtils.closeConnection(conn);
        }
    }

    private int getOrInsertExercise(Exercise ex, Connection conn) throws SQLException {
        // 1. Cerca se esiste già
        String searchSQL = "SELECT id FROM exercise WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(searchSQL)) {
            stmt.setString(1, ex.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // 2. Se non esiste, crealo
        String insertSQL = "INSERT INTO exercise (name, description, muscle_group) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ex.getName());
            stmt.setString(2, ex.getDescription());
            String muscle = (ex.getMuscleGroup() != null) ? ex.getMuscleGroup().name() : "CHEST";
            stmt.setString(3, muscle);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Impossibile salvare l'esercizio locale: " + ex.getName());
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
                //plan.setId(planID);
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
    public void update(WorkoutPlan plan) throws SQLException {
        // Nomi tabelle al singolare
        String updatePlanQuery = "UPDATE workout_plan SET name = ?, comment = ? WHERE id = ?";
        String deleteExercisesQuery = "DELETE FROM workout_exercise WHERE workout_plan_id = ?";
        String insertExerciseQuery = "INSERT INTO workout_exercise (workout_plan_id, exercise_id, sets, reps, rest_time) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psPlan = null;
        PreparedStatement psDelete = null;
        PreparedStatement psInsert = null;

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false); // Transazione ON

            // 1. Aggiorna nome e descrizione della SCHEDA
            psPlan = conn.prepareStatement(updatePlanQuery);
            psPlan.setString(1, plan.getName());
            psPlan.setString(2, plan.getComment());
            psPlan.setInt(3, plan.getId());
            psPlan.executeUpdate();

            // 2. Rimuovi i vecchi esercizi collegati
            psDelete = conn.prepareStatement(deleteExercisesQuery);
            psDelete.setInt(1, plan.getId());
            psDelete.executeUpdate();

            // 3. Inserisci i nuovi esercizi
            psInsert = conn.prepareStatement(insertExerciseQuery);

            for (WorkoutExercise we : plan.getExercises()) {
                // Recupera l'oggetto Exercise (gestendo il doppio campo model)
                Exercise ex = we.getExercise();
                if (ex == null) ex = we.getExerciseDefinition();

                // --- LA CORREZIONE È QUI ---
                // Non ci fidiamo di ex.getId(). Usiamo il metodo helper che cerca l'ID nel DB tramite il nome.
                // Questo risolve l'errore "Foreign Key Constraint Fails"
                int realExerciseId = getOrInsertExercise(ex, conn);
                // ---------------------------

                psInsert.setInt(1, plan.getId());
                psInsert.setInt(2, realExerciseId); // Usiamo l'ID sicuro recuperato dal DB
                psInsert.setInt(3, we.getSets());
                psInsert.setInt(4, we.getReps());
                psInsert.setInt(5, we.getRestTime());
                psInsert.addBatch();
            }
            psInsert.executeBatch();

            conn.commit(); // Tutto ok, conferma

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Qualcosa è andato storto, annulla tutto
            throw e;
        } finally {
            DAOUtils.closeStatement(psPlan);
            DAOUtils.closeStatement(psDelete);
            DAOUtils.closeStatement(psInsert);
            DAOUtils.closeConnection(conn);
        }
    }

    @Override
    public void delete(int planId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtDeleteExercise = null;
        PreparedStatement stmtDeletePlan = null;

        String deleteExerciseQuery = "DELETE FROM workout_exercise WHERE workout_plan_id = ?";
        String deleteWorkoutPlanQuery = "DELETE FROM workout_plan WHERE id = ?";

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // 1. Cancellazione figli (Esercizi)
            stmtDeleteExercise = conn.prepareStatement(deleteExerciseQuery);
            stmtDeleteExercise.setInt(1, planId);
            stmtDeleteExercise.executeUpdate(); // ESEGUIAMO!

            // 2. Cancellazione padre (Scheda)
            stmtDeletePlan = conn.prepareStatement(deleteWorkoutPlanQuery);
            stmtDeletePlan.setInt(1, planId);

            // --- ECCO L'ERRORE CHE AVEVI: MANCAVA QUESTA RIGA! ---
            int rows = stmtDeletePlan.executeUpdate();
            // -----------------------------------------------------

            if (rows == 0) {
                System.out.println("Nessuna scheda trovata con ID: " + planId);
            }

            conn.commit();
            System.out.println("Cancellazione completata per ID: " + planId);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            DAOUtils.closeStatement(stmtDeleteExercise);
            DAOUtils.closeStatement(stmtDeletePlan);
            DAOUtils.closeConnection(conn);
        }
    }
}

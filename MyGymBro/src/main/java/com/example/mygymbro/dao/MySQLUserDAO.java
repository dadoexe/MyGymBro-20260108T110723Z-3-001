package com.example.mygymbro.dao;
import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.PersonalTrainer;
import com.example.mygymbro.model.User;
import com.example.mygymbro.utils.DAOUtils;
import com.example.mygymbro.utils.DBConnect;

import java.sql.*;

public class MySQLUserDAO {

    public User findByUsername(String username, String password) throws SQLException{

        // 1. Definisco la query
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";

        // 2. Inizializza le risorse
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        User user = null; // La dichiariamo qui per poterla ritornare alla fine

        try {
            connection = DBConnect.getConnection();
            stmt = connection.prepareStatement(query);

            // 3. Imposta i parametri
            stmt.setString(1, username);
            stmt.setString(2, password);

            // 4. ESEGUI
            resultSet = stmt.executeQuery();

            // 5. Controlliamo se c'è un risultato
            if (resultSet.next()) {
                // Leggiamo il ruolo per capire CHI stiamo creando
                String role = resultSet.getString("role");

                // Dati comuni a tutti
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome"); // o 'name' e 'email' come nel diagramma
                String email = resultSet.getString("email");
                // LOGICA DI "FABBRICAZIONE" (Factory Logic)
                if ("ATHLETE".equals(role)) {
                    // Recuperiamo dati specifici atleta (gestisci i null se necessario)
                    float weight = resultSet.getFloat("weight");
                    float height = resultSet.getFloat("height");

                    // Istanziamo la classe CONCRETA
                    user = new Athlete(id, username, password, nome, email, cognome, weight, height);

                } else if ("TRAINER".equals(role)) {
                    String certCode = resultSet.getString("certificationCode");

                    // Istanziamo la classe CONCRETA
                    user = new PersonalTrainer(id, username, password, nome, cognome, email, certCode);
                }
            }

        } finally {
            // Chiudi le risorse (o usa DAOUtils se l'hai fatta)
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                // connection.close(); // Dipende se vuoi chiuderla qui
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 6. Ritorniamo l'oggetto (sarà null se login fallito, oppure un Athlete/Trainer)
        return user;
    }

    public void saveAthlete(Athlete athlete) throws SQLException{
        // Corrisponde ai campi in User.java
        String userQuery = "INSERT INTO user (username, password, name, cognome, email) VALUES (?, ?, ?, ?, ?)";
        // Il primo '?' è la Foreign Key (l'ID generato dalla userQuery)
        // Poi inseriamo i campi specifici della classe Athlete
        String athleteQuery = "INSERT INTO athlete (user_id, weight, height, age) VALUES (?, ?, ?, ?)";
        String trainerQuery = "INSERT INTO personal_trainer (user_id, cert_code) VALUES (?, ?)";

        Connection connection = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtAthlete = null;
        ResultSet generatedKeys = null;
        try {
            connection = DBConnect.getConnection();
            connection.setAutoCommit(false);

            stmtUser = connection.prepareStatement(userQuery);
            stmtUser.setString(1, athlete.getUsername());
            stmtUser.setString(2, athlete.getPassword());
            stmtUser.setString(3, athlete.getName());
            stmtUser.setString(4, athlete.getCognome());
            stmtUser.setString(5, athlete.getEmail());
            stmtUser.executeUpdate();

            //recupero id generato
            generatedKeys = stmtUser.getGeneratedKeys();
            int newId = -1; //sentinel value
            if (generatedKeys.next()) {
                newId = generatedKeys.getInt(1);
                athlete.setId(newId); //aggiorno oggetto in memoria
            }else{throw new SQLException("Creazione fallita, nessun id ottenuto");}

            //ora salvo in Athlete
            stmtAthlete = connection.prepareStatement(athleteQuery);
            stmtAthlete.setInt(1, newId);
            stmtAthlete.setFloat(2, athlete.getWeight());
            stmtAthlete.setFloat(3, athlete.getHeight());
            stmtAthlete.setInt(4, athlete.getAge());
            stmtAthlete.executeUpdate();
            connection.commit(); //confermo
        }catch (SQLException e){
            if(connection!=null){connection.rollback();} //rollback in caso di errore
            throw e;
        }finally {
            DAOUtils.close(connection, stmtUser, generatedKeys);
            DAOUtils.closeStatement(stmtAthlete);
        }

    }

    public void saveTrainer(PersonalTrainer trainer) throws SQLException {
        String userQuery = "INSERT INTO user (username, password, name, cognome, email) VALUES (?, ?, ?, ?, ?)";
        String trainerQuery = "INSERT INTO personal_trainer (user_id, cert_code) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtTrainer = null;
        ResultSet generatedKeys = null;

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // --- STEP 1: Salvo in USER (Identico a sopra) ---
            stmtUser = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, trainer.getUsername());
            stmtUser.setString(2, trainer.getPassword());
            stmtUser.setString(3, trainer.getName());
            stmtUser.setString(4, trainer.getCognome());
            stmtUser.setString(5, trainer.getEmail());
            stmtUser.executeUpdate();

            generatedKeys = stmtUser.getGeneratedKeys();
            int newId = -1;
            if (generatedKeys.next()) {
                newId = generatedKeys.getInt(1);
                trainer.setId(newId);
            } else {
                throw new SQLException("Errore ID.");
            }

            // --- STEP 2: Salvo in PERSONAL_TRAINER ---
            stmtTrainer = conn.prepareStatement(trainerQuery);
            stmtTrainer.setInt(1, newId);                 // FK: user_id
            stmtTrainer.setString(2, trainer.getCertCode()); // Campo 'certCode' dallo screen
            stmtTrainer.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            DAOUtils.close(conn, stmtUser, generatedKeys);
            DAOUtils.closeStatement(stmtTrainer);
        }
    }
}

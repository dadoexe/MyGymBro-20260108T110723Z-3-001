package com.example.mygymbro.dao;
import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.PersonalTrainer;
import com.example.mygymbro.model.User;
import com.example.mygymbro.utils.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

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

}

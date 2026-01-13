package com.example.mygymbro.dao;

import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.User;
import java.sql.SQLException; // Importante
import java.util.ArrayList;
import java.util.List;

public class InMemoryUserDAO implements UserDAO {

    private static List<User> ramDB = new ArrayList<>();

    static {
        // Correggi il costruttore in base alla tua classe Athlete reale
        // Esempio generico basato sul tuo codice:
        ramDB.add(new Athlete(1000, "mario123", "password", "Mario", 25, "mario@email.com", "Rossi", 75, 180));
    }

    @Override
    public User findByUsername(String username) throws SQLException { // Aggiunto throws
        return ramDB.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(User user) throws SQLException { // Aggiunto throws
        if (findByUsername(user.getUsername()) != null) {
            // Simuliamo un errore DB se esiste già
            throw new SQLException("Utente già esistente (RAM DB)");
        }
        ramDB.add(user);
    }
}
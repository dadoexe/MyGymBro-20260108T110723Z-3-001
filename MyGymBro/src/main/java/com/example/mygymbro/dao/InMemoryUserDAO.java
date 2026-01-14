package com.example.mygymbro.dao;

import com.example.mygymbro.model.User;
import com.example.mygymbro.model.Athlete;
import com.example.mygymbro.model.PersonalTrainer;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserDAO implements UserDAO {

    // Questo è il nostro "Database in RAM". Statico per sopravvivere ai cambi di scena.
    private static List<User> ramDB = new ArrayList<>();

    // Blocco statico per avere almeno un utente di prova subito
    static {
        ramDB.add(new Athlete(1000, "mario123", "password", "Mario", 23, "test@test.com","Rossi",78, 180));
    }

    @Override
    public User findByUsername(String username) {
        // Simulo la SELECT con uno Stream
        return ramDB.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null); // Ritorna null se non lo trova
    }

    @Override
    public void save(User user) {
        // Controllo duplicati (opzionale ma realistico)
        if (findByUsername(user.getUsername()) != null) {
            System.out.println("Utente già esistente nel DB RAM!");
            return; // O lancia eccezione
        }
        // Simulo la INSERT
        ramDB.add(user);
        System.out.println("Utente salvato in RAM: " + user.getUsername());
    }
}
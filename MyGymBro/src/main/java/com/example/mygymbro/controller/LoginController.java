package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.MySQLUserDAO;
import com.example.mygymbro.dao.UserDAO;
import com.example.mygymbro.model.User;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class LoginController implements Controller {

    private UserDAO userDAO;

    public LoginController() {
        this.userDAO = new MySQLUserDAO();
    }

    public void checkLogin(UserBean credentials) throws LoginException {

        // 1. VALIDAZIONE INIZIALE (Spostata all'inizio!)
        // Bisogna controllare che i dati ci siano PRIMA di usarli o chiamare il DB.
        if(credentials.getUsername() == null || credentials.getUsername().isEmpty() ||
                credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
            throw new LoginException("Campi vuoti: inserisci username e password.");
        }

        User user = null;
        try {
            // 2. RECUPERO UTENTE (Solo username)
            user = userDAO.findByUsername(credentials.getUsername());
        } catch (SQLException e) {
            e.printStackTrace(); // Log per te
            throw new LoginException("Errore di connessione al Database.");
        }

        // 3. VERIFICA PASSWORD
        // Se l'utente è null (non esiste) o la password non coincide...
        if (user == null || !user.getPassword().equals(credentials.getPassword())) {
            // Messaggio utente, NON "errore db" (che confonde), ma "Credenziali errate"
            throw new LoginException("Credenziali non valide.");
        }

        // 4. MAPPING DEI DATI (L'errore critico era qui!)
        UserBean loggedUser = new UserBean();
        // Prendo username/password da dove voglio (credentials o user sono uguali ora)
        loggedUser.setUsername(user.getUsername());

        // ATTENZIONE: Questi dati li devo prendere da 'user' (Database),
        // NON da 'credentials' (Input Utente)!
        // 'credentials' ha solo user e pass, gli altri campi sono null.
        loggedUser.setNome(user.getName());       // <--- Corretto
        loggedUser.setCognome(user.getCognome()); // <--- Corretto
        loggedUser.setEmail(user.getEmail());     // <--- Corretto

        // Se vuoi salvare anche il ruolo o l'id:
        // loggedUser.setId(user.getId());

        // 5. SESSIONE E NAVIGAZIONE (Corretti i Typos)
        SessionManager.getInstance().login(loggedUser); // 'S' maiuscola
        ApplicationController.getInstance().loadHome(); // 'Controller' corretto
    }

    @Override
    public void dispose() {
        // Ricordati di implementarlo vuoto se richiesto dall'interfaccia
    }
}




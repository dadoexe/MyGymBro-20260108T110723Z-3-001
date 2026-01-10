package com.example.mygymbro.controller;

import com.example.mygymbro.dao.MySQLUserDAO;
import com.example.mygymbro.dao.UserDAO;
import com.example.mygymbro.model.User;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class LoginController implements Controller {

    // 1. Usa l'Interfaccia, non la classe concreta (Dependency Injection)
    private UserDAO userDAO;

    public LoginController() {
        // 2. Istanzia il DAO qui
        this.userDAO = new MySQLUserDAO();
    }

    // Niente DTO. Se va tutto bene, il metodo finisce (void).
    // Se va male, lancia un'eccezione.
    public void autentica(String username, String password) throws LoginException, SQLException {

        // Validazione input
        if (username == null || password == null) {
            throw new LoginException("Campi vuoti");
        }

        // 3. Chiamata al metodo d'istanza (userDAO.find...)
        // Errore tuo corretto: passo 'username', non 'String username'
        User user = userDAO.findByUsername(username, password);

        // Verifica password
        if (user != null && user.getPassword().equals(password)) {

            // 4. È il Controller Applicativo che setta la sessione!
            SessionManager.getInstance().login(user);

            // 5. È il Controller Applicativo che cambia pagina!
            ApplicationController.getInstance().loadHome();

        } else {
            // Se le credenziali non vanno, lancio l'eccezione
            throw new LoginException("Credenziali non valide");
        }
    }


    @Override
    public void dispose() {

    }
}

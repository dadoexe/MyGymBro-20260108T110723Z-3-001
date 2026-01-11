package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.MySQLUserDAO;
import com.example.mygymbro.dao.UserDAO;
import com.example.mygymbro.model.User;
import com.example.mygymbro.view.LoginView;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;

public class LoginController implements Controller {

    private UserDAO userDAO;// interfaccia per parlare col db
    private LoginView view; //interfaccia per parlare con la grafica

    public LoginController(LoginView view) {
        this.userDAO = new MySQLUserDAO();
        this.view = view;
    }

    public void checkLogin(UserBean credentials) throws LoginException {

        // 1. VALIDAZIONE INIZIALE

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
            e.printStackTrace();
            throw new LoginException("Errore di connessione al Database.");
        }

        // 3. VERIFICA PASSWORD
        // Se l'utente è null (non esiste) o la password non coincide...
        if (user == null || !user.getPassword().equals(credentials.getPassword())) {

            throw new LoginException("Credenziali non valide.");
        }

        // 4. MAPPING DEI DATI
        UserBean loggedUser = new UserBean();
        // Prendo username/password da dove voglio (credentials o user sono uguali ora)
        loggedUser.setUsername(user.getUsername());

        // Questi dati li devo prendere da 'user' (Database),
        // NON da 'credentials' (Input Utente)
        // 'credentials' ha solo user e pass, gli altri campi sono null.
        loggedUser.setNome(user.getName());
        loggedUser.setCognome(user.getCognome());
        loggedUser.setEmail(user.getEmail());

         loggedUser.setId(user.getId());

        // 5. SESSIONE E NAVIGAZIONE
        SessionManager.getInstance().login(loggedUser);
        ApplicationController.getInstance().loadHome();
    }

    @Override
    public void dispose() {
        this.userDAO = null;
    }
}




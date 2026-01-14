package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.MySQLUserDAO;
import com.example.mygymbro.dao.UserDAO;
import com.example.mygymbro.model.User;
import com.example.mygymbro.views.LoginView;

import java.sql.SQLException;

public class LoginController implements Controller {

    private UserDAO userDAO;// interfaccia per parlare col db
    private LoginView view; //interfaccia per parlare con la grafica

    public LoginController(LoginView view) {
        this.userDAO = new MySQLUserDAO();
        this.view = view;
    }

    public void validateLogin(String username, String password) {
        System.out.println("3. CONTROLLER: Ho ricevuto i dati! User: " + username +" " +password); // <--- SPIA 3
        // 1. VALIDAZIONE INIZIALE (Input vuoti)
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            view.showMessage("Inserisci username e password.");
            return; // Interrompiamo subito
        }

        try {
            // 2. RECUPERO UTENTE DAL DB (Tramite Model)
            User userModel = userDAO.findByUsername(username);

            // 3. VERIFICA PASSWORD
            // Nota: in un'app reale useresti hash (BCrypt), qui va bene equals()
            if (userModel == null || !userModel.getPassword().equals(password)) {
                view.showMessage("Credenziali non valide.");
                return;
            }

            // 4. MAPPING MODEL -> BEAN (Solo ora creiamo il Bean!)
            // Questo è il momento giusto: abbiamo i dati veri dal DB.
            UserBean userBean = new UserBean();
            userBean.setId(userModel.getId());
            userBean.setUsername(userModel.getUsername());
            userBean.setNome(userModel.getName());
            userBean.setCognome(userModel.getCognome());
            userBean.setEmail(userModel.getEmail());
            // Non settiamo la password nel Bean di sessione per sicurezza, o se serve sì.

            // 5. SALVATAGGIO IN SESSIONE
            SessionManager.getInstance().login(userBean);

            // 6. NAVIGAZIONE VERSO LA HOME
            ApplicationController.getInstance().loadHome();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showMessage("Errore di connessione al database.");
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        this.userDAO = null;
    }
}




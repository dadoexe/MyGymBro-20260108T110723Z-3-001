package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.DAOFactory;
import com.example.mygymbro.dao.UserDAO;

import com.example.mygymbro.model.User;

import com.example.mygymbro.views.LoginView;


import java.sql.SQLException;


public class LoginController implements Controller {

    private UserDAO userDAO;
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        // Usiamo la Factory per ottenere il DAO corretto
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void checkLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            view.showError("Inserisci username e password.");
            return;
        }

        try {
            // 1. Recupera l'utente dal DAO (che ora distingue User e PersonalTrainer!)
            User userModel = userDAO.findByUsername(username);

            if (userModel == null || !userModel.getPassword().equals(password)) {
                view.showError("Credenziali non valide.");
                return;
            }

            // 2. Riempi il Bean
            UserBean userBean = new UserBean();
            userBean.setId(userModel.getId());
            userBean.setUsername(userModel.getUsername());
            userBean.setNome(userModel.getName());
            userBean.setCognome(userModel.getCognome());
            userBean.setEmail(userModel.getEmail());

            // --- 3. ASSEGNA IL RUOLO ---
            if (userModel instanceof com.example.mygymbro.model.PersonalTrainer) {
                userBean.setRole("TRAINER");
            } else {
                userBean.setRole("ATHLETE");
            }

            // 4. Salva in sessione
            SessionManager.getInstance().login(userBean);

            view.showSuccess("Login effettuato! Ruolo: " + userBean.getRole());

            // 5. CHIAMA LO SMISTATORE (Non più loadHome diretta!)
            ApplicationController.getInstance().loadHomeBasedOnRole();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showError("Errore Database.");
        }
    }

    @Override
    public void dispose() {
        this.userDAO = null;
    }


}

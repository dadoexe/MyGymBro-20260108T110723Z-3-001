package com.example.mygymbro.controller;

import com.example.mygymbro.bean.UserBean;
import com.example.mygymbro.dao.DAOFactory; // <--- IMPORTANTE
import com.example.mygymbro.dao.UserDAO;
import com.example.mygymbro.model.User;
import com.example.mygymbro.views.LoginView;

import java.sql.SQLException;

public class LoginController implements Controller {

    private UserDAO userDAO;
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
        // MODIFICA 1: Usiamo la Factory, non l'implementazione diretta!
        // Così se attivi la modalità DEMO in DAOFactory, qui funziona tutto in automatico.
        this.userDAO = DAOFactory.getUserDAO();
    }

    // MODIFICA 2: Il metodo si chiama checkLogin e NON prende parametri.
    // È il controller che chiede alla view i dati inseriti.
    public void checkLogin() {

        // Recuperiamo i dati tramite l'interfaccia (funziona sia per GUI che per CLI)
        String username = view.getUsername();
        String password = view.getPassword();

        System.out.println("DEBUG: Tentativo login per user: " + username);

        // 1. VALIDAZIONE INIZIALE (Input vuoti)
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            view.showMessage("Inserisci username e password.");
            return;
        }

        try {
            // 2. RECUPERO UTENTE DAL DB
            User userModel = userDAO.findByUsername(username);

            // 3. VERIFICA PASSWORD
            if (userModel == null || !userModel.getPassword().equals(password)) {
                view.showMessage("Credenziali non valide.");
                return;
            }

            // 4. MAPPING MODEL -> BEAN
            UserBean userBean = new UserBean();
            // ATTENZIONE: Assicurati che UserBean accetti String o int per l'ID in base al tuo DB
            // Se userModel.getId() è int e userBean vuole String, fai String.valueOf(...)
            userBean.setId(userModel.getId());
            userBean.setUsername(userModel.getUsername());
            userBean.setNome(userModel.getName());
            userBean.setCognome(userModel.getCognome());
            userBean.setEmail(userModel.getEmail());

            // 5. SALVATAGGIO IN SESSIONE
            SessionManager.getInstance().login(userBean);

            view.showMessage("Login effettuato con successo!"); // Opzionale

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


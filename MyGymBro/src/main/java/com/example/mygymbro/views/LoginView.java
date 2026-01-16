package com.example.mygymbro.views;

import com.example.mygymbro.controller.LoginController;

public interface LoginView {
    String getUsername();
    String getPassword();
    void setListener(LoginController controller);
    void showMessage(String message);
}

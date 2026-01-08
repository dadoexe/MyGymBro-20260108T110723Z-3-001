package com.example.mygymbro.dao;

import com.example.mygymbro.model.User;

import java.sql.SQLException;

public interface UserDAO {

    User findByUsernameLogin(String username, String password) throws SQLException; // login
    void save(User user);
}

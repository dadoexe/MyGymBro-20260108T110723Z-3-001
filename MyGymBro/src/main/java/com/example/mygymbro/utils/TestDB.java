package com.example.mygymbro.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        // Sostituisci con i TUOI dati esatti
        String url = "jdbc:mysql://localhost:3306/mygymbro";
        String user = "root";
        String pass = "";

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            if (conn != null) {
                System.out.println("SUCCESSO! Connessione avvenuta.");
            }
        } catch (SQLException e) {
            System.err.println("FALLITO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
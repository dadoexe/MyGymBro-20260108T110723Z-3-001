package com.example.mygymbro.model;

public abstract class User { // <--- ABSTRACT!
    private int id;
    private String username;
    private String password;
    private String name;
    private String cognome;
    private String email; // Nel diagramma c'è email, non cognome

    // Costruttore per le sottoclassi
    public User(int id, String username, String password, String name, String cognome, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.cognome= cognome;
        this.email = email;
    }

    // Getter e Setter...

    public String getUsername() {
        return username;
    }
}
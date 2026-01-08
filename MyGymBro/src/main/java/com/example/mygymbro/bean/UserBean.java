package com.example.mygymbro.bean;

public class UserBean {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String email;

    // Costruttore vuoto
    public UserBean() {}

    // Getter e Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

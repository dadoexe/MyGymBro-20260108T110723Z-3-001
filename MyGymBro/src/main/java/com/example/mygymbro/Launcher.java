package com.example.mygymbro;

public class Launcher {
    public static void main(String[] args) {
        // Invece di Application.launch, chiamiamo direttamente il main della nostra classe Main
        Main.main(args);
        System.out.println("Launcher started the application.");
    }
}

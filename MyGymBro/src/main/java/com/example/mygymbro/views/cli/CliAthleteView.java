package com.example.mygymbro.views.cli;

import com.example.mygymbro.bean.WorkoutExerciseBean;
import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.NavigationController;
import com.example.mygymbro.views.AthleteView;

import java.util.List;
import java.util.Scanner;

public class CliAthleteView implements AthleteView, CliView {

    private NavigationController listener;
    private Scanner scanner;

    // Cache locale per ricordare le schede scaricate
    private List<WorkoutPlanBean> myPlansCache;

    public CliAthleteView() {
        this.scanner = new Scanner(System.in);
    }

    // --- METODI View ---
    @Override public void showSuccess(String msg) { System.out.println("[OK] " + msg); }
    @Override public void showError(String msg) { System.out.println("[ERR] " + msg); }

    @Override
    public void run() {
        // 1. FIX DOPPIA STAMPA: Carichiamo i dati UNA SOLA VOLTA fuori dal loop
        if (listener != null) {
            listener.loadDashboardData();
        }

        boolean stay = true;
        while (stay) {
            // Menu principale pulito
            System.out.println("\n=== MENU PRINCIPALE ===");
            System.out.println("1. Crea Nuova Scheda");
            System.out.println("2. Gestisci le tue schede (Visualizza/Modifica/Elimina)");
            System.out.println("0. Logout");
            System.out.print("Scelta > ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    if (listener != null) listener.loadWorkoutBuilder();
                    stay = false;
                    break;
                case "2":
                    // Entriamo nel sottomenu di gestione
                    handleManagePlans();
                    break;
                case "0":
                    if (listener != null) listener.logout();
                    stay = false;
                    break;
                default:
                    System.out.println("Comando non valido.");
            }
        }
    }

    // --- LOGICA DI GESTIONE SCHEDE ---
    private void handleManagePlans() {
        if (myPlansCache == null || myPlansCache.isEmpty()) {
            System.out.println("\n(Non hai ancora nessuna scheda salvata)");
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println("\n--- SELEZIONA UNA SCHEDA ---");
            for (int i = 0; i < myPlansCache.size(); i++) {
                System.out.println((i + 1) + ". " + myPlansCache.get(i).getName());
            }
            System.out.println("0. Indietro al Menu Principale");
            System.out.print("Numero > ");

            try {
                int selection = Integer.parseInt(scanner.nextLine());

                if (selection == 0) {
                    managing = false; // Esce dal loop e torna al menu principale
                } else if (selection > 0 && selection <= myPlansCache.size()) {
                    // Recuperiamo la scheda scelta
                    WorkoutPlanBean selectedPlan = myPlansCache.get(selection - 1);

                    // MOSTRA I DETTAGLI (Esercizi, ecc.)
                    printPlanDetails(selectedPlan);

                    // CHIEDI AZIONE
                    askActionForPlan(selectedPlan);

                    // Se abbiamo eliminato o modificato, usciamo per ricaricare i dati
                    // (Opzionale: potremmo ricaricare e restare qui, ma uscire è più sicuro)
                    managing = false;
                } else {
                    System.out.println("Numero non valido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input non valido.");
            }
        }
    }

    private void printPlanDetails(WorkoutPlanBean plan) {
        System.out.println("\n==========================================");
        System.out.println(" SCHEDA: " + plan.getName().toUpperCase());
        System.out.println(" Descrizione: " + (plan.getComment() != null ? plan.getComment() : "---"));
        System.out.println("==========================================");

        List<WorkoutExerciseBean> exercises = plan.getExerciseList();
        if (exercises == null || exercises.isEmpty()) {
            System.out.println(" (Nessun esercizio presente)");
        } else {
            System.out.printf("%-25s | %-10s | %-5s | %-5s\n", "Esercizio", "Gruppo", "SetxRep", "Rec");
            System.out.println("------------------------------------------------------------");
            for (WorkoutExerciseBean ex : exercises) {
                String setRep = ex.getSets() + "x" + ex.getReps();
                System.out.printf("%-25s | %-10s | %-7s | %ds\n",
                        truncate(ex.getExerciseName(), 25),
                        ex.getMuscleGroup(),
                        setRep,
                        ex.getRestTime());
            }
        }
        System.out.println("==========================================\n");
    }

    private void askActionForPlan(WorkoutPlanBean plan) {
        System.out.println("Cosa vuoi fare con questa scheda?");
        System.out.println("1. MODIFICA (Aggiungi/Rimuovi esercizi)");
        System.out.println("2. ELIMINA DEFINITIVAMENTE");
        System.out.println("0. Nulla (Indietro)");
        System.out.print("Azione > ");

        String action = scanner.nextLine();
        switch (action) {
            case "1":
                System.out.println("Caricamento builder...");
                if (listener != null) listener.modifyPlan(plan);
                break;
            case "2":
                System.out.print("Sei VERAMENTE sicuro? (scrivi 'si'): ");
                if (scanner.nextLine().equalsIgnoreCase("si")) {
                    if (listener != null) listener.deletePlan(plan);
                    System.out.println(">>> Scheda eliminata.");
                }
                break;
            default:
                System.out.println("Nessuna azione effettuata.");
        }
    }

    // Helper per tagliare stringhe troppo lunghe nella tabella
    private String truncate(String str, int width) {
        if (str == null) return "";
        if (str.length() > width) return str.substring(0, width - 3) + "...";
        return str;
    }

    // --- IMPLEMENTAZIONE INTERFACCIA ---

    @Override
    public void setListener(NavigationController listener) {
        this.listener = listener;
    }

    @Override
    public void updateWelcomeMessage(String msg) {
        // Stampiamo solo se è la prima volta o se serve davvero
        System.out.println("Benvenuto " + msg);
    }

    @Override
    public void updateWorkoutList(List<WorkoutPlanBean> workoutPlans) {
        // Questo metodo viene chiamato dal Controller quando i dati sono pronti.
        // Ci limitiamo a salvare la lista. NON stampiamo nulla qui per evitare doppioni.
        this.myPlansCache = workoutPlans;
    }
}
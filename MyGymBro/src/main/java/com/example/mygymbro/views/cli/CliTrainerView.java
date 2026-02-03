package com.example.mygymbro.views.cli;

import com.example.mygymbro.bean.AthleteBean;
import com.example.mygymbro.bean.WorkoutPlanBean;
import com.example.mygymbro.controller.TrainerController;
import com.example.mygymbro.views.TrainerView;

import java.util.List;
import java.util.Scanner;

public class CliTrainerView implements TrainerView, CliView {

    private TrainerController listener;
    private final Scanner scanner;

    // Dati locali per il menu CLI
    private List<AthleteBean> cachedAthletes;
    private List<WorkoutPlanBean> cachedPlans;

    // Selezione corrente
    private AthleteBean selectedAthlete;
    private WorkoutPlanBean selectedPlan;

    public CliTrainerView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        boolean running = true;

        // Carica i dati iniziali appena parte la vista
        if (listener != null) listener.loadDashboardData();

        while (running) {
            System.out.println("\n--- DASHBOARD TRAINER ---");
            System.out.println("Cliente Selezionato: " + (selectedAthlete != null ? selectedAthlete.getUsername() : "NESSUNO"));
            System.out.println("-------------------------");
            System.out.println("1. Visualizza Lista Clienti");
            System.out.println("2. Seleziona Cliente (per operare)");
            System.out.println("3. Visualizza Schede Cliente");
            System.out.println("4. Assegna Nuova Scheda");
            System.out.println("5. Modifica Scheda Esistente");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    if (listener != null) listener.loadDashboardData(); // Ricarica e stampa
                    break;
                case "2":
                    handleSelectAthlete();
                    break;
                case "3":
                    if (checkSelection()) {
                        listener.loadPlansForAthlete(selectedAthlete);
                    }
                    break;
                case "4":
                    if (checkSelection()) {
                        listener.createNewPlan();
                        // Nota: Qui il flusso passerà al WorkoutBuilderView (CLI o GUI)
                        // Quando tornerà indietro, running sarà ancora true
                    }
                    break;
                case "5":
                    handleModifyPlan();
                    break;
                case "0":
                    running = false;
                    listener.logout();
                    break;
                default:
                    System.out.println("Opzione non valida.");
            }
        }
    }

    // --- LOGICA CLI INTERNA ---

    private void handleSelectAthlete() {
        if (cachedAthletes == null || cachedAthletes.isEmpty()) {
            System.out.println("⚠️ Nessun cliente in lista. Premi '1' per aggiornare.");
            return;
        }

        System.out.println("Inserisci l'ID del cliente da selezionare:");
        // Per semplicità stampiamo indice e nome, ma usiamo l'ID reale o l'indice lista
        for (int i = 0; i < cachedAthletes.size(); i++) {
            System.out.println((i + 1) + ". " + cachedAthletes.get(i).getUsername());
        }

        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < cachedAthletes.size()) {
                this.selectedAthlete = cachedAthletes.get(index);
                System.out.println("✅ Cliente selezionato: " + selectedAthlete.getUsername());
                // Carichiamo subito le schede per averle pronte
                listener.loadPlansForAthlete(selectedAthlete);
            } else {
                System.out.println("Indice non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero.");
        }
    }

    private void handleModifyPlan() {
        if (!checkSelection()) return;

        if (cachedPlans == null || cachedPlans.isEmpty()) {
            System.out.println("⚠️ Questo cliente non ha schede da modificare.");
            return;
        }

        System.out.println("Quale scheda vuoi modificare?");
        for (int i = 0; i < cachedPlans.size(); i++) {
            System.out.println((i + 1) + ". " + cachedPlans.get(i).getName());
        }

        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < cachedPlans.size()) {
                this.selectedPlan = cachedPlans.get(index);
                listener.modifySelectedPlan();
            } else {
                System.out.println("Indice non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero.");
        }
    }

    private boolean checkSelection() {
        if (selectedAthlete == null) {
            System.out.println("⚠️ DEVI PRIMA SELEZIONARE UN CLIENTE (Opzione 2)");
            return false;
        }
        return true;
    }

    // --- IMPLEMENTAZIONE INTERFACCIA TRAINERVIEW ---

    @Override
    public void setListener(TrainerController controller) {
        this.listener = controller;
    }

    @Override
    public void showAthletesList(List<AthleteBean> athletes) {
        this.cachedAthletes = athletes;
        System.out.println("\n--- LISTA CLIENTI ---");
        if (athletes.isEmpty()) {
            System.out.println("(Nessun cliente trovato)");
        } else {
            for (AthleteBean a : athletes) {
                System.out.println("👤 " + a.getUsername() + " (" + a.getNome() + " " + a.getCognome() + ")");
            }
        }
    }

    @Override
    public void showAthletePlans(List<WorkoutPlanBean> plans) {
        this.cachedPlans = plans;
        System.out.println("\n--- SCHEDE DI " + (selectedAthlete != null ? selectedAthlete.getUsername() : "???") + " ---");
        if (plans == null || plans.isEmpty()) {
            System.out.println("(Nessuna scheda assegnata)");
        } else {
            for (WorkoutPlanBean p : plans) {
                System.out.println("📄 " + p.getName() + " - " + p.getComment());
            }
        }
    }

    @Override
    public void updateWelcomeMessage(String msg) {
        System.out.println("Benvenuto Coach " + msg + "!");
    }

    @Override
    public AthleteBean getSelectedAthlete() {
        return this.selectedAthlete;
    }

    @Override
    public WorkoutPlanBean getSelectedPlan() {
        return this.selectedPlan;
    }

    @Override
    public void showSuccess(String msg) {
        System.out.println("✅ SUCCESSO: " + msg);
    }

    @Override
    public void showError(String msg) {
        System.out.println("❌ ERRORE: " + msg);
    }

    // Metodo legacy per compatibilità

    public void updateAthletePrograms(List<WorkoutPlanBean> workoutPlans) {
        showAthletePlans(workoutPlans);
    }
}
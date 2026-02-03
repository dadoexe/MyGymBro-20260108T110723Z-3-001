package com.example.mygymbro.dao;

/**
 * Factory Pattern
 * Questa classe è l'unico punto dell'applicazione che conosce la verità:
 * stiamo usando un Database vero o stiamo fingendo (Demo)?
 */
public class DAOFactory {

    // --- MODIFICA 1: Rimosso 'final' così possiamo cambiarlo a runtime ---
    private static boolean isDemoMode = false;

    // --- MODIFICA 2: Aggiunto il metodo Setter per il Launcher ---
    public static void setDemoMode(boolean active) {
        isDemoMode = active;
        System.out.println("DAOFactory: Modalità DEMO impostata su " + active);
    }

    /**
     * Restituisce l'implementazione corretta per la gestione Utenti
     */
    public static UserDAO getUserDAO() {
        if (isDemoMode) {
            return new InMemoryUserDAO();
        } else {
            return new MySQLUserDAO();
        }
    }

    /**
     * Restituisce l'implementazione corretta per i Piani di Allenamento
     */
    public static WorkoutPlanDAO getWorkoutPlanDAO() {
        if (isDemoMode) {
            return new InMemoryWorkoutPlanDAO();
        } else {
            return new MySQLWorkoutPlanDAO();
        }
    }

    /**
     * Restituisce l'implementazione per gli Esercizi.
     */
    public static ExerciseDAO getExerciseDAO() {
        // L'API esterna va bene per entrambi i casi
        return new RestApiExerciseDAO();
    }
}

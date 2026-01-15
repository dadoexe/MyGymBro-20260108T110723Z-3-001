package com.example.mygymbro.dao;

/**
 * Factory Pattern
 * Questa classe è l'unico punto dell'applicazione che conosce la verità:
 * stiamo usando un Database vero o stiamo fingendo (Demo)?
 */
public class DAOFactory {

    // --- INTERRUTTORE GENERALE ---
    // TRUE  = Versione Demo (Dati in RAM, si resettano alla chiusura)
    // FALSE = Versione Produzione (Dati su MySQL + API Reale)
    private static final boolean IS_DEMO_VERSION = false;

    /**
     * Restituisce l'implementazione corretta per la gestione Utenti
     */
    public static UserDAO getUserDAO() {
        if (IS_DEMO_VERSION) {
            return new InMemoryUserDAO();
        } else {
            return new MySQLUserDAO();
        }
    }

    /**
     * Restituisce l'implementazione corretta per i Piani di Allenamento
     */
    public static WorkoutPlanDAO getWorkoutPlanDAO() {
        if (IS_DEMO_VERSION) {
            return new InMemoryWorkoutPlanDAO();
        } else {
            return new MySQLWorkoutPlanDAO();
        }
    }

    /**
     * Restituisce l'implementazione per gli Esercizi.
     * * NOTA: Qui la logica è leggermente diversa.
     * L'API esterna (RestApiExerciseDAO) è "Read-Only" e non richiede un DB locale,
     * quindi è perfetta sia per la Demo che per la versione Reale.
     * Se però volessi usare il vecchio DB MySQL locale, puoi cambiare il ramo else.
     */
    public static ExerciseDAO getExerciseDAO() {
        if (IS_DEMO_VERSION) {
            // Nella Demo usiamo comunque l'API perché è figa e non richiede setup database
            return new RestApiExerciseDAO();
        } else {
            // In Produzione usiamo l'API
            return new RestApiExerciseDAO();

            // OPPURE: se volessi tornare al vecchio DB locale:
            // return new MySQLExerciseDAO();
        }
    }
}

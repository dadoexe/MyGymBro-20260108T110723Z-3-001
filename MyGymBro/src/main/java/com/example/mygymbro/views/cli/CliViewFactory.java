package com.example.mygymbro.views.cli;

import com.example.mygymbro.views.*;

public class CliViewFactory implements ViewFactory {

    @Override
    public LoginView createLoginView() {
        return new CliLoginView(); // Assicurati di aver creato questa classe
    }

    @Override
    public WorkoutBuilderView createWorkoutBuilderView() {
        return new CliWorkoutBuilderView(); // Quella che abbiamo abbozzato prima
    }


    @Override
    public AthleteView createAthleteView() {
        return new CliAthleteView();


    }

    public TrainerView createTrainerView() {
        return new CliTrainerView();
    }
}
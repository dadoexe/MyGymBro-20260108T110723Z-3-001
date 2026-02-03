package com.example.mygymbro.views.gui;

import com.example.mygymbro.views.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class GraphicViewFactory implements ViewFactory {

    @Override
    public LoginView createLoginView() {
        try {
            // Assicurati che il percorso dell'FXML sia corretto!
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/schermataLogin.fxml"));
            Parent root = loader.load();
            GraphicLoginView view = loader.getController();
            view.setRoot(root);
            return view;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public WorkoutBuilderView createWorkoutBuilderView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/workout_builder.fxml"));
            Parent root = loader.load();
            GraphicWorkoutBuilderView view = loader.getController();
            view.setRoot(root);
            return view;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

     @Override
    public AthleteView createAthleteView() {
       try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/mainpage.fxml"));
              Parent root = loader.load();
              GraphicAthleteView view = loader.getController();
              view.setRoot(root); //
           return view;
       }catch (IOException e){
           e.printStackTrace();
           return null;
       }
    }
    @Override
    public TrainerView createTrainerView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/view/trainer_view.fxml"));
            Parent root = loader.load();
            GraphicTrainerView view = loader.getController();
            view.setRoot(root);
            return view;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
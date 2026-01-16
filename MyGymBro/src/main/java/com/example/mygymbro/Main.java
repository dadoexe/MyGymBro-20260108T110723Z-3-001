package com.example.mygymbro;

import com.example.mygymbro.controller.ApplicationController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. IMPOSTA LA MODALITÀ (Esplicito è meglio!)
        //ApplicationController.getInstance().setGraphicMode(true);

        // 2. PASSA IL COMANDO AL CONTROLLER
        ApplicationController.getInstance().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
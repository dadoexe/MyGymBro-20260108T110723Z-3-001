package com.example.mygymbro.controller;

import com.example.mygymbro.view.LoginView;
import com.example.mygymbro.view.AthleteView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public static class ApplicationController implements Controller {//singleton

    //static variable reference of istance
    private static ApplicationController instance = null;

     //private contructor restricted to this class
    private ApplicationController() {}

    public static synchronized ApplicationController getInstance() {
        if (instance == null) {
            instance= new ApplicationController();}
            return instance;

    }

    // --- 2. GESTIONE DELLO STAGE E DEL CONTROLLER ATTUALE ---
    private Stage mainStage;
    private Controller currentController; // L'interfaccia generica che abbiamo creato



public void start(Stage primaryStage){
    this.mainStage = primaryStage;
    //carichiamo la prima schermata
    loadLogin();
    this.mainStage.show();
}
//METODI DI NAVIGAZIONE
public void loadLogin(){
try{if(currentController!=null){

   currentController.dispose();}
    //carico FXML
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/home.fxml"));
    Parent root = loader.load();

    //recupero view grafica
    AthleteView view = loader.getController();
    //creao un controller applicativo
    //istanzio il navigation controler
    NavigationController controler = new NavigationController();
    //update controller
    this.currentController = controller;
    //show
    mainStage.setTitle("MyGymBro - Home");
    mainStage.setScene(new Scene(root));

}catch(IOException e){e.printStackTrace();}

}

    public void logout() {
        // Pulisco la sessione
        SessionManager.getInstance().logout();
        // Torno al login
        loadLogin();
    }

public void loadHome(){}
public void loadWorkoutBuilder(){}

public void dispose(){}
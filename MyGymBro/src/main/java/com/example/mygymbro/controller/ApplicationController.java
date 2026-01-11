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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/schermataLogin.fxml"));
        Parent root = loader.load();

        //recupero view grafica
        LoginView view = loader.getController();
        //creao un controller applicativo
        //istanzio il controller applicativo del login
        LoginController controller = new LoginController(view);
        view.setListener(controller);
        //update controller
        this.currentController = controller;
    //show
    mainStage.setTitle("MyGymBro - Login");
    mainStage.setScene(new Scene(root));
    mainStage.show();

}catch(IOException e){e.printStackTrace();}

}


public void loadHome(){
try{if(currentController!=null){
    currentController.dispose();
}//carico fxml
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/mygymbro/view/mainpage.fxml"));
    Parent root = loader.load();
    //recupero la view grafica dell atleta
    AthleteView view = loader.getController();
    //creao un controller applicativo per la mia home che è rappresentata da "AthleteView"
    //ha come controller applicativo il "navigationController"
    NavigationController controller = new NavigationController(view);
    //wiring
    view.setListener(controller);
    this.currentController = controller;

    //show
    mainStage.setTitle("MyGymBro - Home");
    mainStage.setScene(new Scene(root));
    mainStage.show();
        }catch(IOException e){e.printStackTrace();}
}


public void loadWorkoutBuilder(){

}

    public void logout() {
        // Pulisco la sessione
        SessionManager.getInstance().logout();
        // Torno al login
        loadLogin();
    }

public void dispose() {}

}
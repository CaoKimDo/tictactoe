package com.triplet.tictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @SuppressWarnings("unused")
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/primary.fxml"));
        Scene scene = new Scene(root, 800, 800);
        //Image icon = new Image(getClass().getResource("images/App's icon.png").toString());

        stage.setScene(scene);
        //stage.getIcons().add(icon);
        stage.setTitle("Tic-tac-toe");
        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        //GameController.gameController.stage = stage;  // Save & pass the created stage to GameController
        
        stage.show();
    }
}
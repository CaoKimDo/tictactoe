package com.triplet.tictactoe;

import java.awt.Desktop;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainMenuController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private void setRootSceneStage(ActionEvent event, String root) throws Exception{
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML  // "Join a room" button -> "JoinMenu" scene
    public void joinARoom(ActionEvent event) throws Exception {
        Player.setIsHost(false);
        setRootSceneStage(event, "JoinMenu");
    }

    @FXML  // "Host a new room" button -> "HostMenu" scene
    public void hostANewRoom(ActionEvent event) throws Exception {
        Player.setIsHost(true);
        setRootSceneStage(event, "HostMenu");
    }
    
    @FXML  // "Ranking" button -> "Ranking" scene
    public void Ranking(ActionEvent event) throws Exception {
        setRootSceneStage(event, "primary");
    }
    
    @FXML  // "More information & How to play" button -> Open the URL in web browser
    public void Information(ActionEvent event) throws Exception {
        Desktop.getDesktop().browse(new URI("https://github.com/CaoKimDo/tictactoe"));
    }
    
    @FXML  // "Quit" button -> Confirm & close the game
    public void Quit(ActionEvent event) throws Exception {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        
        alert.setTitle("Confirm Quit");
        alert.setHeaderText("You are about to quit the game.");
        alert.setContentText("Are you sure you want to exit?");
        
        // Set custom icon
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResource("images/App's icon.png").toString()));
        
        if (alert.showAndWait().get() == ButtonType.OK)
            System.exit(0);
    }
}
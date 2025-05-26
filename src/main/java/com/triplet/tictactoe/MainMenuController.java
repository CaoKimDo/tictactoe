package com.triplet.tictactoe;

import java.awt.Desktop;
import java.io.IOException;
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
    
    @FXML  // "Join a room" button -> "JoinMenu" scene
    private void joinARoom(ActionEvent event) throws IOException {
        Player.setIsHostAndPlayerMark(false, "O");
        setRootSceneStage(event, "JoinMenu");
    }

    @FXML  // "Host a new room" button -> "HostMenu" scene
    private void hostANewRoom(ActionEvent event) throws IOException {
        Player.setIsHostAndPlayerMark(true, "X");
        setRootSceneStage(event, "HostMenu");
    }
    
    @FXML  // "Ranking" button -> "Ranking" scene
    private void Ranking(ActionEvent event) {
        
    }
    
    @FXML  // "More information & How to play" button -> Open the URL in web browser
    private void Information(ActionEvent event) throws Exception {
        Desktop.getDesktop().browse(new URI("https://github.com/CaoKimDo/tictactoe"));
    }
    
    @FXML  // "Quit" button -> Confirm & close the game
    private void Quit(ActionEvent event) {
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
    
    private void setRootSceneStage(ActionEvent event, String root) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);
        
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
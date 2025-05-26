package com.triplet.tictactoe;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ServerConnectionController {
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
}
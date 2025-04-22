package com.triplet.tictactoe;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SecondaryController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private void switchToPrimary(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("fxml/primary.fxml"));
        scene = new Scene(root, 800, 800);
        
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
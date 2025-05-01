package com.triplet.tictactoe;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HostController {
    private static BooleanProperty validPlayer, validRoom;

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private TextField roomNameTextField;

    @FXML
    private TextField roomPasswordTextField;

    @FXML
    private TextField gridWidthTextField;

    @FXML
    private TextField gridHeightTextField;

    @FXML
    private TextField winConditionTextField;

    @FXML
    private TextField gameTimeTextField;

    @FXML
    private TextField hostPlayerNameTextField;

    public static void setValidPlayer(boolean value) {
        validPlayer.set(value);
    }

    public static void setValidRoom(boolean value) {
        validRoom.set(value);
    }

    private void setRootSceneStage(ActionEvent event, String root) throws Exception{
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML  // "Host a new game" button -> "Waiting" scene
    @SuppressWarnings("unused")
    public void host(ActionEvent event) throws Exception {
        String roomName = roomNameTextField.getText();
        String roomPassword = roomPasswordTextField.getText();
        int gridWidth = Integer.parseInt(gridWidthTextField.getText());
        int gridHeight = Integer.parseInt(gridHeightTextField.getText());
        int winCondition = Integer.parseInt(winConditionTextField.getText());
        int gameTime = Integer.parseInt(gameTimeTextField.getText());
        String hostPlayerName = hostPlayerNameTextField.getText();
        
        validPlayer = new SimpleBooleanProperty(false);
        validRoom = new SimpleBooleanProperty(false);
        
        Player.createPlayer(hostPlayerName);  // Create a new Player with the given name for the host player
        // Notify the server to create a new room with the provided information
        Player.createRoom(roomName, roomPassword, gridWidth, gridHeight, winCondition, gameTime, hostPlayerName);
        Player.joinRoom(roomName);  // Join the created room
        
        validPlayer.and(validRoom).addListener((observable, oldValue, newValue) -> {
            if (newValue)
                Platform.runLater(() -> {
                    try {
                        setRootSceneStage(event, "Waiting");  // Switch to "Waiting" scene
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        });
    }
    
    @FXML  // "Return" button -> "MainMenu" scene
    public void returnMainMenu(ActionEvent event) throws Exception {
        setRootSceneStage(event, "MainMenu");
    }
}
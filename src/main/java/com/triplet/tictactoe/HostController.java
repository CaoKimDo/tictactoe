package com.triplet.tictactoe;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HostController {
    private static BooleanProperty validPlayer = new SimpleBooleanProperty();
    private static BooleanProperty validRoom = new SimpleBooleanProperty();

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
    
    @FXML  // "Host a new room" button -> "Waiting" scene
    @SuppressWarnings("unused")
    private void host(ActionEvent event) {
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
        // Notify the server to create a new room with the provided information & join it
        Player.createAndJoinRoom(roomName, roomPassword, gridWidth, gridHeight, winCondition, gameTime, hostPlayerName);
        
        validPlayer.and(validRoom).addListener((observable, oldValue, newValue) -> {
            System.out.println("[JoinController] validPlayer & validRoom: " + newValue);  // Logging
            if (newValue)
                Platform.runLater(() -> {
                    try {
                        setRootSceneStage(event, "Waiting");  // Switch to "Waiting" scene
                        
                        stage.setOnCloseRequest(eventOnClose -> {
                            Alert alert = new Alert(AlertType.CONFIRMATION);
                            
                            alert.setTitle("Confirm Quit");
                            alert.setHeaderText("You are about to quit the game.");
                            alert.setContentText("Are you sure you want to exit?");
                            
                            // Set custom icon
                            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                            alertStage.getIcons().add(new Image(getClass().getResource("images/App's icon.png").toString()));
                            
                            if (alert.showAndWait().get() == ButtonType.OK) {
                                Player.leaveRoom();
                                Player.closeRoom();
                                Player.disconnect();

                                System.exit(0);
                            } else
                                eventOnClose.consume();  // Prevents window from closing
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        });
    }
    
    @FXML  // "Return" button -> "MainMenu" scene
    private void returnMainMenu(ActionEvent event) throws IOException {
        setRootSceneStage(event, "MainMenu");
    }

    private void setRootSceneStage(ActionEvent event, String root) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
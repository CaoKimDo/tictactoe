package com.triplet.tictactoe;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class WaitingController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // Create a listener to observe changes to the roomInfo
    private ListChangeListener<String> roomInfoListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(Change<? extends String> c) {
            Platform.runLater(() -> {
                roomInfoListView.refresh();
            });
        }
    };
    
    // Create a listener to observe changes to the roomPlayers
    private ListChangeListener<String> roomPlayersListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(Change<? extends String> c) {
            Platform.runLater(() -> {
                roomPlayersListView.refresh();
            });
        }
    };
    
    @FXML
    private ListView<String> roomInfoListView;
    
    @FXML
    private ListView<String> roomPlayersListView;

    @FXML
    private Button letsPlayButton;

    @FXML
    private Button closeOrLeaveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListViews();

        letsPlayButton.setVisible(Player.checkIsHost());

        if (Player.checkIsHost())
            closeOrLeaveButton.setText("Close this room");
        else
            closeOrLeaveButton.setText("Leave this room");
    }

    private void setListViews() {
        // Binding
        roomInfoListView.setItems(App.roomInfo);
        roomPlayersListView.setItems(App.roomPlayers);

        // Add listeners
        App.roomInfo.addListener(roomInfoListener);
        App.roomPlayers.addListener(roomPlayersListener);
    }

    private void removeListeners() {
        App.roomInfo.removeListener(roomInfoListener);
        App.roomPlayers.removeListener(roomPlayersListener);
    }

    @FXML  // "LET'S PLAY!" button -> "Game" scene
    public void letsPlay() {
        removeListeners();
        System.out.println("[WaitingController] LET'S PLAY button is hit.");  // Logging
    }
    
    @FXML  // "closeOrLeaveButton" button -> "MainMenu" scene
    public void closeOrLeave(ActionEvent event) throws Exception {
        Player.leaveRoom();
        
        if (Player.checkIsHost()) {
            Player.closeRoom();
            root = FXMLLoader.load(getClass().getResource("fxml/MainMenu.fxml"));
        } else
            root = FXMLLoader.load(getClass().getResource("fxml/JoinMenu.fxml"));
            
        Player.disconnect();
        
        removeListeners();
        
        scene = new Scene(root);
        
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
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
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class WaitingController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    
    @FXML
    private ListView<String> roomInfoListView;
    
    @FXML
    private ListView<String> playersListView;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setRoomInfoListView();  // Set roomInfoListView
        setPlayersListView();  // Set playersListView
    }

    private void setRoomInfoListView() {
        roomInfoListView.getItems().setAll(Player.roomInfo);
    }
    
    private void setPlayersListView() {
        playersListView.setItems(Player.playerNamesList);  // Binding
        
        // Add a listener to observe changes to the playerNamesList
        Player.playerNamesList.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> change) {
                Platform.runLater(() -> {
                    playersListView.refresh();
                });
            }
        });
    }
    
    @FXML  // "LET'S PLAY!" button -> "Game" scene
    public void letsPlay() {
        
    }
    
    @FXML  // "Close this room" button -> "MainMenu" scene
    public void returnMainMenu(ActionEvent event) throws Exception {
        // Implement send close room request

        root = FXMLLoader.load(getClass().getResource("fxml/MainMenu.fxml"));
        scene = new Scene(root);
        
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
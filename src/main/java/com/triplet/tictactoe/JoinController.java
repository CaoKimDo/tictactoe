package com.triplet.tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class JoinController implements Initializable {
    private static BooleanProperty validPlayer = new SimpleBooleanProperty();
    private static BooleanProperty validJoin = new SimpleBooleanProperty();

    private Stage stage;
    private Scene scene;
    private Parent root;

    // Create a listener to observe changes to the roomList
    private ListChangeListener<String> roomListListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(Change<? extends String> c) {
            Platform.runLater(() -> {
                roomListListView.refresh();
                Player.viewRoom(selectedRoom);
            });
        }
    };

    // Create a listener to observe changes to the roomInfo
    private ListChangeListener<String> roomInfoListener = new ListChangeListener<String>() {
        @Override
        public void onChanged(Change<? extends String> c) {
            Platform.runLater(() -> {
                roomInfoListView.refresh();
            });
        }
    };

    private String selectedRoom;

    @FXML
    private ListView<String> roomListListView;

    @FXML
    private ListView<String> roomInfoListView;

    @FXML
    private TextField roomPasswordTextField;

    @FXML
    private TextField playerNameTextField;

    public static void setValidPlayer(boolean value) {
        validPlayer.set(value);
    }
    
    public static void setValidJoin(boolean value) {
        validJoin.set(value);
    }
    
    @Override
    @SuppressWarnings("unused")
    public void initialize(URL location, ResourceBundle resources) {
        setListViews();

        roomListListView.setOnMouseClicked(event -> {
            selectedRoom = roomListListView.getSelectionModel().getSelectedItem();  // Get selected room's name
            Player.viewRoom(selectedRoom);
        });

        Player.getRoomList();

        Platform.runLater(() -> {
            App.roomInfo.clear();
        });

        selectedRoom = null;
    }

    @FXML  // "JOIN" button -> "Waiting" scene
    @SuppressWarnings("unused")
    private void join(ActionEvent event) {
        validPlayer = new SimpleBooleanProperty(false);
        validJoin = new SimpleBooleanProperty(false);
        
        // Create a new Player with the given name for the player
        if (!playerNameTextField.getText().isEmpty())
            Player.createPlayer(playerNameTextField.getText());

        // Join the created room
        if (!roomPasswordTextField.getText().isEmpty())
            Player.joinRoom(selectedRoom, roomPasswordTextField.getText());
        else
            Player.joinRoom(selectedRoom, "!TripleT@1029384756Default");
            
        validPlayer.and(validJoin).addListener((observable, oldValue, newValue) -> {
            System.out.println("[JoinController] validPlayer & validJoin: " + newValue);  // Logging
            if (newValue)
                Platform.runLater(() -> {
                    try {
                        removeListeners();

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
        removeListeners();
        setRootSceneStage(event, "MainMenu");
    }

    private void setListViews() {
        // Binding
        roomListListView.setItems(App.roomList);
        roomInfoListView.setItems(App.roomInfo);

        // Add listeners
        App.roomList.addListener(roomListListener);
        App.roomInfo.addListener(roomInfoListener);
    }
    
    private void removeListeners() {
        App.roomList.removeListener(roomListListener);
        App.roomInfo.removeListener(roomInfoListener);
    }
    
    private void setRootSceneStage(ActionEvent event, String root) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
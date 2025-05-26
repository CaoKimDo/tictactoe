package com.triplet.tictactoe;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameController {
    private static GameController gameController = new GameController();
    private static int gridWidth, gridHeight, winCondition;

    private Board board;
    
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button surrenderOrReturnButton;

    @FXML
    private Label roomLabel;

    @FXML
    private Label boardSizeLabel;

    @FXML
    private Label playerXLabel;

    @FXML
    private Label playerOLabel;

    @FXML
    private Label turnLabel;

    @FXML
    private Label winConditionLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label reasonLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private GridPane boardGridPane;

    @FXML
    private ScrollPane boardScrollPane;

    @FXML
    private VBox chatVBox;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private Button sendButton;
    
    @FXML
    void handleSendClicked(MouseEvent event) {
        String message = messageTextArea.getText().trim().replace("\n", "\\n");

        if (!message.isEmpty()) {
            Player.sendMessage(message);
            messageTextArea.clear();
        }
    }
    
    @FXML
    void handleSurrenderOrReturnClicked(MouseEvent event) {
        if (surrenderOrReturnButton.getText().equals("Surrender")) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            
            alert.setTitle("Confirm Surrender");
            alert.setHeaderText("Are you sure you want to surrender?");
            alert.setContentText("This action cannot be undone.");
            
            // Set custom icon
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResource("images/App's icon.png").toString()));
            
            if (alert.showAndWait().get() == ButtonType.OK)
                Player.surrender();
            else
                event.consume();
        } else
            Player.returnMenu();
    }

    public static GameController getGameController() {
        return gameController;
    }

    public void start() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Game.fxml"));
        
        root = loader.load();
        scene = new Scene(root);

        stage = App.getAppStage();
        stage.setScene(scene);
        stage.show();
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight()) / 2);
        
        gameController = loader.getController();
        gameController.init();
    }
    
    private void init() {
        roomLabel.setText("Room: " + Player.getPlayerRoom());  // Set roomLabel (room name)
        boardSizeLabel.setText("Board size: " + gridWidth + " x " + gridHeight);  // Set boardSizeLabel
        playerXLabel.setText("X: " + App.roomPlayers.get(0));  // Set playerXLabel (who host)
        playerOLabel.setText("O: " + App.roomPlayers.get(1));  // Set playerOLabel (who join)
        winConditionLabel.setText(winCondition + " in a row to win!");  // Set winConditionLabel
        statusLabel.setText("Game started!");  // Set statusLabel
        
        board = new Board(gridWidth, gridHeight, winCondition);  // Initialize the playing board
        
        // Initialize messageTextArea Shift+Enter & Enter events
        messageTextArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown())
                    messageTextArea.insertText(messageTextArea.getCaretPosition(), "\n");  // Insert "\n" at caret position when Shift & Enter are pressed
                else {
                    String message = messageTextArea.getText().trim().replace("\n", "\\n");  // trim() removes leading & trailing whitespace characters
                    
                    if (!message.isEmpty()) {
                        Player.sendMessage(message);
                        messageTextArea.clear();
                    }
                }

                event.consume();  // This prevents default Enter behavior (new line)
            }
        });
        
        Player.ready();  // Notify the server that the player is ready to play
    }
    
    public Board getBoard() {
        return board;
    }
    
    public GridPane getBoardGridPane() {
        return boardGridPane;
    }

    public String getsurrenderOrReturnButtonText() {
        return surrenderOrReturnButton.getText();
    }
    
    public String getTimerLabelText() {
        return timerLabel.getText();
    }
    
    public void setRootSceneStage(String root) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource("fxml/" + root + ".fxml"));
        scene = new Scene(this.root);
        
        Platform.runLater(() -> {
            stage = App.getAppStage();
            stage.setScene(scene);
            stage.setOnCloseRequest(eventOnClose -> {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                
                alert.setTitle("Confirm Quit");
                alert.setHeaderText("You are about to quit the game.");
                alert.setContentText("Are you sure you want to exit?");
                
                // Set custom icon
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResource("images/App's icon.png").toString()));
                
                if (alert.showAndWait().get() == ButtonType.OK) {
                    if (root.equals("Waiting")) {
                        Player.leaveRoom();
                        Player.closeRoom();
                        Player.disconnect();
                    }
                    
                    System.exit(0);
                } else
                    eventOnClose.consume();  // Prevents window from closing
            });
            stage.show();
            stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2);
            stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight()) / 2);
        });
    }

    public void setBoardInfo(int gridWidth, int gridHeight, int winCondition) {
        GameController.gridWidth = gridWidth;
        GameController.gridHeight = gridHeight;
        GameController.winCondition = winCondition;
    }

    public void setSurrenderOrReturnButton(String command) {
        Platform.runLater(() -> {
            surrenderOrReturnButton.setText(command);
        });
    }
    
    public void setTurnLabel(String turn) {
        Platform.runLater(() -> {
            turnLabel.setText(turn);
        });
    }

    public void setStatusLabel(String status) {
        Platform.runLater(() -> {
            statusLabel.setText(status);
        });
    }

    public void setReasonLabel(String reason) {
        Platform.runLater(() -> {
            reasonLabel.setText(reason);
            reasonLabel.setVisible(true);
        });
    }

    public void setTimerLabel(String timer) {
        Platform.runLater(() -> {
            timerLabel.setText(timer);
        });
    }

    public void displayMessage(String playerName, String message) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 10, 5, 10));

        Text text = new Text(message);
        text.setFont(Font.font("Segoe UI", 14));

        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        textFlow.setMaxWidth(291);

        if (Player.getPlayerName().equals(playerName)) {
            text.setFill(Color.web("#EFF2FF"));  // Set the text color

            hbox.setAlignment(Pos.CENTER_RIGHT);
            textFlow.setStyle("-fx-background-color: #0084FF; -fx-background-radius: 20px");
        } else {
            hbox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setStyle("-fx-background-color: #E9E9EB; -fx-background-radius: 20px");
        }

        hbox.getChildren().add(textFlow);
        
        Platform.runLater(() -> {
            chatVBox.getChildren().add(hbox);
        });
    }
}
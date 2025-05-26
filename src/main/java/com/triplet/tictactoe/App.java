package com.triplet.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    public static ObservableList<RankingData> rankingDataList = FXCollections.observableArrayList();
    public static ObservableList<String> roomList = FXCollections.observableArrayList();
    public static ObservableList<String> roomInfo = FXCollections.observableArrayList();
    public static ObservableList<String> roomPlayers = FXCollections.observableArrayList();
    
    private static final int SERVER_PORT = 4848;
    private static final int BROADCAST_PORT = 8484;
    
    private static InetAddress serverIp;
    
    private static Socket socket;
    private static InputStreamReader inputStreamReader;
    private static OutputStreamWriter outputStreamWriter;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;

    private static Stage appStage;
    
    public static void send(String messageToBeSent) {
        try {
            bufferedWriter.write(messageToBeSent);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public static Stage getAppStage() {
        appStage.setOnCloseRequest(eventOnClose -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            
            alert.setTitle("Confirm Quit");
            alert.setHeaderText("You are about to quit the game. If you do so, your opponent will win this game.");
            alert.setContentText("Are you sure you want to exit?");
            
            // Set custom icon
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(App.class.getResource("images/App's icon.png").toString()));
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                if (GameController.getGameController().getsurrenderOrReturnButtonText().equals("Surrender"))
                    Player.surrender();

                Player.shutdown();
            } else
                eventOnClose.consume();  // Prevents window from closing
        });
        
        return appStage;
    }
    
    private static void waitForServerConnection() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket(BROADCAST_PORT);  // Listen on BROADCAST_PORT for broadcast
            
            byte[] receivedBuffer = new byte[256];
            DatagramPacket receivedDatagramPacket = new DatagramPacket(receivedBuffer, receivedBuffer.length);

            System.out.println("[App] Waiting for server broadcast...");  // Logging
            datagramSocket.receive(receivedDatagramPacket);  // Wait for server broadcast
            
            String serverMessage = new String(receivedDatagramPacket.getData()).trim();
            System.out.println("[App] Received broadcast: " + serverMessage);  // Logging

            // Extract IP from broadcast message
            serverIp = receivedDatagramPacket.getAddress();
            System.out.println("[App] Server IP: " + serverIp.getHostAddress());  // Logging
            
            datagramSocket.close();  // Close the UDP socket after receiving the broadcast
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initCommunication() {
        try {
            socket = new Socket(serverIp, SERVER_PORT);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listen() {
        while (socket.isConnected()) {
            try {
                String[] messagePart = bufferedReader.readLine().split("\\|");  // Read the received message (Blocking command)
                String key = messagePart[0];

                switch (key) {
                    case "ranking":
                        RankingData rankingData = new RankingData(Integer.parseInt(messagePart[1]), messagePart[2], Integer.parseInt(messagePart[3]));
                        Platform.runLater(() -> {
                            rankingDataList.add(rankingData);
                        });

                        break;
                    case "roomList":
                        Platform.runLater(() -> {
                            roomList.setAll(Arrays.asList(messagePart).subList(1, messagePart.length));
                        });
                        
                        System.out.println("[App] roomList: " + roomList);  // Logging
                        break;
                    case "roomInfo":
                        Platform.runLater(() -> {
                            roomInfo.setAll(Arrays.asList(messagePart).subList(1, messagePart.length));
                        });

                        System.out.println("[App] roomInfo: " + roomInfo);  // Logging
                        break;
                    case "roomPlayers":
                        Platform.runLater(() -> {
                            roomPlayers.setAll(Arrays.asList(messagePart).subList(1, messagePart.length));
                        });
                        
                        System.out.println("[App] roomPlayers: " + roomPlayers);  // Logging
                        break;
                    case "boardInfo":
                        GameController.getGameController().setBoardInfo(Integer.parseInt(messagePart[1]), Integer.parseInt(messagePart[2]), Integer.parseInt(messagePart[3]));
                        
                        Platform.runLater(() -> {
                            try {
                                GameController.getGameController().start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        System.out.println("[App] boardInfo");  // Logging
                        break;
                    case "timer":
                        int remainingTimeInSeconds = Integer.parseInt(messagePart[1]);
                        int h = remainingTimeInSeconds / 3600;
                        int m = remainingTimeInSeconds % 3600 / 60;
                        int s = remainingTimeInSeconds % 60;

                        String remainingTime;
                        if (h > 0)
                            remainingTime = h + "h " + m + "m";
                        else if (m > 0)
                            remainingTime = m + "m " + s + "s";
                        else
                            remainingTime = s + "s";
                            
                        GameController.getGameController().setTimerLabel(remainingTime);
                        break;
                    case "turn":
                        if (Player.getPlayerName().equals(messagePart[1])) {
                            GameController.getGameController().setTurnLabel("Your turn!");
                            GameController.getGameController().getBoardGridPane().setMouseTransparent(false);
                        } else
                            GameController.getGameController().setTurnLabel("Opponent's turn.");
                            
                        System.out.println("[App] turn");  // Logging
                        break;
                    case "mark":
                        if (Player.getPlayerMark().equals("X"))
                            GameController.getGameController().getBoard().markButton(Integer.parseInt(messagePart[1]), Integer.parseInt(messagePart[2]), "O");
                        else
                            GameController.getGameController().getBoard().markButton(Integer.parseInt(messagePart[1]), Integer.parseInt(messagePart[2]), "X");

                        System.out.println("[App] mark");  // Logging
                        break;
                    case "message":
                        GameController.getGameController().displayMessage(messagePart[1], messagePart[2].replace("\\n", "\n"));
                        
                        System.out.println("[App] message");  // Logging
                        break;
                    case "win":
                        if (Player.getPlayerName().equals(messagePart[1]))
                            GameController.getGameController().setStatusLabel("You are the winner!");
                        else
                            GameController.getGameController().setStatusLabel("The opponent wins!");

                        GameController.getGameController().setSurrenderOrReturnButton("Return");
                            
                        System.out.println("[App] win");  // Logging
                        break;
                    case "full":
                        GameController.getGameController().setStatusLabel("Draw!");
                        GameController.getGameController().setReasonLabel("(The board is full.)");
                        GameController.getGameController().setSurrenderOrReturnButton("Return");

                        System.out.println("[App] full");  // Logging
                        break;
                    case "timeIsUp":
                        GameController.getGameController().getBoardGridPane().setMouseTransparent(true);
                        GameController.getGameController().setStatusLabel("Draw!");
                        GameController.getGameController().setReasonLabel("(No one wins because the time is up.)");
                        GameController.getGameController().setSurrenderOrReturnButton("Return");
                        
                        System.out.println("[App] timeIsUp");  // Logging
                        break;
                    case "surrender":
                        GameController.getGameController().getBoardGridPane().setMouseTransparent(true);

                        if (Player.getPlayerName().equals(messagePart[1])) {
                            GameController.getGameController().setStatusLabel("You lose!");
                            GameController.getGameController().setReasonLabel("(Because you surrendered)");
                        } else {
                            GameController.getGameController().setStatusLabel("You win!");
                            GameController.getGameController().setReasonLabel("(The opponent surrendered.)");
                        }

                        GameController.getGameController().setSurrenderOrReturnButton("Return");

                        System.out.println("[App] surrender");  // Logging
                        break;
                    case "return":
                        if (Player.checkIsHost())
                            GameController.getGameController().setRootSceneStage("Waiting");  // Switch to "Waiting" scene
                        else {
                            Player.leaveRoom();
                            Player.disconnect();

                            GameController.getGameController().setRootSceneStage("MainMenu");  // Swith to "MainMenu" scene
                        }

                        System.out.println("[App] return");  // Logging
                        break;
                    case "shutdown":
                        if (Player.getPlayerName().equals(messagePart[1])) {
                            Player.leaveRoom();

                            if (Player.checkIsHost())
                                Player.closeRoom();

                            Player.disconnect();

                            System.exit(0);
                        } else {
                            if (Player.checkIsHost())
                                GameController.getGameController().setRootSceneStage("Waiting");  // Switch to "Waiting" scene
                            else
                                GameController.getGameController().setRootSceneStage("MainMenu");  // Swith to "MainMenu" scene
                        }
                        
                        break;
                    case "RANKING_UPDATE":
                        Platform.runLater(() -> {
                            rankingDataList.clear();
                        });
                        
                        break;
                    case "VALID_PLAYER":
                        if (Player.checkIsHost())
                            HostController.setValidPlayer(true);
                        else
                            JoinController.setValidPlayer(true);

                        System.out.println("[App] validPlayer = true");  // Logging
                        break;
                    case "VALID_ROOM":
                        HostController.setValidRoom(true);

                        System.out.println("[App] validRoom = true");  // Logging
                        break;
                    case "VALID_JOIN":
                        JoinController.setValidJoin(true);

                        System.out.println("[App] validJoin = true");  // Logging
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/ServerConnection.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image(getClass().getResource("images/App's icon.png").toString());
        
        // Stage setup
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.setTitle("TripleT Tic-tac-toe");
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            
            alert.setTitle("Confirm Quit");
            alert.setHeaderText("You are about to quit the game.");
            alert.setContentText("Are you sure you want to exit?");
            
            // Set custom icon
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResource("images/App's icon.png").toString()));
            
            if (alert.showAndWait().get() == ButtonType.OK)
                System.exit(0);
            else
                event.consume();  // Prevents window from closing
        });
        stage.show();
        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth()) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight()) / 2);

        appStage = stage;  // This will be later used by the GameController
        
        // Start connection logic when the "ServerConnection" scene is loaded
        new Thread(() -> {
            waitForServerConnection();
            
            // Switch to "MainMenu" scene once the connection is successful
            Platform.runLater(() -> {
                try {
                    Parent mainMenuRoot = FXMLLoader.load(getClass().getResource("fxml/MainMenu.fxml"));
                    
                    stage.setScene(new Scene(mainMenuRoot));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            initCommunication();
            listen();
        }).start();
    }
}
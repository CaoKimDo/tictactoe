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
import javafx.stage.Stage;

public class App extends Application {
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
    
    public static void send(String messageToBeSent) {
        try {
            bufferedWriter.write(messageToBeSent);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
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
    public void start(Stage stage) throws Exception {
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
package com.triplet.tictactoe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    private static final int SERVER_PORT = 4848;
    private static final int BROADCAST_PORT = 8484;

    private static InetAddress serverIp;
    
    public static InetAddress getServerIp() {
        return serverIp;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
    
    private void waitForServerConnection() {
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
    
    @SuppressWarnings("unused")
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/ServerConnection.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image(getClass().getResource("images/App's icon.png").toString());
        
        // Stage setup
        stage.setScene(scene);
        stage.getIcons().add(icon);
        stage.setTitle("TripleT Tic-tac-toe");
        stage.setOnCloseRequest(event -> {
            System.exit(0);
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
        }).start();
    }
}
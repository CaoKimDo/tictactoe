package com.triplet.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Player {
    private static Socket socket;
    private static InputStreamReader inputStreamReader;
    private static OutputStreamWriter outputStreamWriter;
    private static BufferedReader bufferedReader;
    private static BufferedWriter bufferedWriter;
    private static ObjectInputStream objectInputStream;
    private static ObjectOutputStream objectOutputStream;

    public static ArrayList<String> roomInfo;
    public static ObservableList<String> playerNamesList = FXCollections.observableArrayList();

    private static String playerName;
    private static String playerRoom;

    private static void listenForUpdate() {

    }

    public static void init(InetAddress serverIp, int serverPort, String playerName) {
        try {
            socket = new Socket(serverIp, serverPort);
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            
            Player.playerName = playerName;  // Set player's name

            // Notify the server about the new player
            bufferedWriter.write("NEW_PLAYER|" + Player.playerName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // The listenForUpdate method will keep running on a new thread
        new Thread(() -> listenForUpdate()).start();
    }

    @SuppressWarnings("unchecked")
    public static void createRoom(String roomName, String roomPassword, int gridWidth, int gridHeight, int winCondition, int gameTime, String hostPlayerName) {
        try {
            bufferedWriter.write("NEW_ROOM|" + roomName + "|" + roomPassword + "|" + Integer.toString(gridWidth) + "|" + Integer.toString(gridHeight) + "|" + Integer.toString(winCondition) + "|" + Integer.toString(gameTime) + "|" + hostPlayerName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            roomInfo = (ArrayList<String>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
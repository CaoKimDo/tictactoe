package com.triplet.tictactoe;

import javafx.application.Platform;

public class Player {
    private static String playerName;
    private static String playerRoom;

    public static void createPlayer(String playerName) {
        Player.playerName = playerName;  // Set player's name
        App.send("NEW_PLAYER|" + Player.playerName);  // Notify the server about the new player
    }

    public static void disconnect() {
        App.send("REMOVE_PLAYER");
    }

    public static void createRoom(String roomName, String roomPassword, int gridWidth, int gridHeight, int winCondition, int gameTime, String hostPlayerName) {
        App.send("NEW_ROOM|" + roomName + "|" + roomPassword + "|" + Integer.toString(gridWidth) + "|" + Integer.toString(gridHeight) + "|" + Integer.toString(winCondition) + "|" + Integer.toString(gameTime) + "|" + hostPlayerName);
    }
    
    public static void joinRoom(String roomName) {
        playerRoom = roomName;  // Set player's room
        App.send("JOIN_ROOM|" + playerRoom);
    }

    public static void leaveRoom() {
        App.roomInfo.clear();
        Platform.runLater(() -> {
            App.playerNamesList.clear();
        });
        App.send("LEAVE_ROOM");
    }

    public static void closeRoom() {
        App.send("CLOSE_ROOM");
    }
}
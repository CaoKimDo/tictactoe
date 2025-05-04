package com.triplet.tictactoe;

public class Player {
    private static String playerName;
    private static String playerRoom;

    private static boolean isHost;

    public static boolean checkIsHost() {
        return isHost;
    }
    
    public static void setIsHost(boolean value) {
        isHost = value;
    }

    public static void createPlayer(String playerName) {
        Player.playerName = playerName;  // Set player's name
        App.send("NEW_PLAYER|" + Player.playerName);
    }

    public static void disconnect() {
        App.send("REMOVE_PLAYER");
    }

    public static void createAndJoinRoom(String roomName, String roomPassword, int gridWidth, int gridHeight, int winCondition, int gameTime, String hostPlayerName) {
        playerRoom = roomName;  // Set player's room
        App.send("CREATE_AND_JOIN_ROOM|" + roomName + "|" + roomPassword + "|" + Integer.toString(gridWidth) + "|" + Integer.toString(gridHeight) + "|" + Integer.toString(winCondition) + "|" + Integer.toString(gameTime) + "|" + hostPlayerName);
    }
    
    public static void joinRoom(String roomName, String roomPassword) {
        playerRoom = roomName;  // Set player's room
        App.send("JOIN_ROOM|" + roomName + "|" + roomPassword);
    }
    
    public static void leaveRoom() {
        App.send("LEAVE_ROOM");
    }

    public static void closeRoom() {
        App.send("CLOSE_ROOM|" + playerRoom);
    }
    
    public static void viewRoom(String selectedRoom) {
        App.send("VIEW_ROOM|" + selectedRoom);
    }
    
    public static void getRoomList() {
        App.send("GET_ROOMLIST");
    }
}
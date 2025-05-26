package com.triplet.tictactoe;

public class Player {
    private static String playerName;
    private static String playerRoom;
    private static String playerMark;

    private static boolean isHost;

    public static boolean checkIsHost() {
        return isHost;
    }

    public static String getPlayerName() {
        return playerName;
    }
    
    public static String getPlayerMark() {
        return playerMark;
    }

    public static String getPlayerRoom() {
        return playerRoom;
    }
    
    public static void setIsHostAndPlayerMark(boolean value, String playerMark) {
        isHost = value;
        Player.playerMark = playerMark;
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

    public static void start() {
        App.send("START");
    }

    public static void ready() {
        App.send("READY");
    }

    public static void mark(int row, int column) {
        App.send("MARK|" + row + "|" + column);
    }

    public static void turn() {
        App.send("TURN");
    }

    public static void sendMessage(String message) {
        App.send("MESSAGE|" + playerName + "|" + message);
    }

    public static void win() {
        App.send("WIN");
    }

    public static void full() {
        App.send("FULL");
    }

    public static void surrender() {
        App.send("SURRENDER");
    }

    public static void returnMenu() {
        App.send("RETURN");
    }

    public static void shutdown() {
        App.send("SHUTDOWN");
    }
}
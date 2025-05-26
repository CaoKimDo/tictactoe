package com.triplet.tictactoe;

import javafx.application.Platform;
import javafx.scene.control.Button;

public class Board {
    // Properties & components of the playing board
    private int width, height, winCondition;  // Board's width, height & winCondition
    private Button[][] grid;  // Board's data (a 2D array of buttons)

    public Board(int gridWidth, int gridHeight, int winCondition) {
        width = gridWidth;
        height = gridHeight;
        this.winCondition = winCondition;

        grid = new Button[height][width];

        initializeGridPane();
    }

    public void markButton(int row, int column, String mark) {
        Platform.runLater(() -> {
            grid[row][column].setText(mark);  // Set it with the opponent's mark
            grid[row][column].setDisable(true);  // Make it non-selectable
        });
    }

    private void initializeGridPane() {
        GameController.getGameController().getBoardGridPane().setMouseTransparent(true);
        GameController.getGameController().getBoardGridPane().getChildren().clear();
        
        for (int row = 0; row < height; ++row)
            for (int column = 0; column < width; ++column) {
                grid[row][column] = createButton(row, column);
                GameController.getGameController().getBoardGridPane().add(grid[row][column], column, row);
            }

        System.out.println("[Board] boardGridPane initialization completed");  // Logging
    }
    
    // Method for creating a new button & setting its on-click function
    @SuppressWarnings("unused")
    private Button createButton(int row, int column) {
        Button button = new Button();  // Create a new button
        
        double buttonSize = Math.max(50, (500 - 21 - (Math.max(width, height) - 1) * 5) / Math.max(width, height));
        button.setPrefSize(buttonSize, buttonSize);  // Set button's size
        button.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");  // Set button's font size & weight
        button.setFocusTraversable(false);
        
        button.setOnAction(event -> {
            // If the cell hasn't been selected
            if (grid[row][column].getText().isEmpty()) {
                grid[row][column].setText(Player.getPlayerMark());  // Set it with the player's mark
                grid[row][column].setDisable(true);  // Make it non-selectable
                GameController.getGameController().getBoardGridPane().setMouseTransparent(true);  // Player cannot make further selection

                Player.mark(row, column);

                if (!GameController.getGameController().getTimerLabelText().equals("0s")) {
                    if (checkWinner())
                        Player.win();
                    else if (checkIsFull())
                        Player.full();
                    else
                        Player.turn();
                }
            }
        });

        return button;
    }
    
    private boolean checkWinner() {
        int[][] DIRECTIONS = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};  // 4 directions (Right, Down, Down-right, Down-left)

        for (int row = 0; row < height; ++row)
            for (int column = 0; column < width; ++column)
                if (!grid[row][column].getText().isEmpty())
                    for (int[] direction : DIRECTIONS) {
                        int count = 1;
                        int r = row + direction[0], c = column + direction[1];
                        
                        while (r >= 0 && r < height && c >= 0 && c < width && !grid[r][c].getText().isEmpty() && grid[r][c].getText().equals(grid[row][column].getText())) {
                            ++count;

                            if (count == winCondition)
                                return true;

                            r += direction[0];
                            c += direction[1];
                        }
                    }

        return false;
    }

    private boolean checkIsFull() {
        for (int row = 0; row < height; ++row)
            for (int column = 0; column < width; ++column)
                if (grid[row][column].getText().isEmpty())
                    return false;
                    
        return true;
    }
}
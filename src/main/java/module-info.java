module com.triplet.tictactoe {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.triplet.tictactoe to javafx.fxml;
    exports com.triplet.tictactoe;
}
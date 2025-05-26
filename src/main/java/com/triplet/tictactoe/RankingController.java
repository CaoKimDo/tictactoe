package com.triplet.tictactoe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class RankingController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // Create a listener to observe changes to the rankingDataList
    private ListChangeListener<RankingData> rankingDataListListener = new ListChangeListener<RankingData>() {
        @Override
        public void onChanged(Change<? extends RankingData> c) {
            Platform.runLater(() -> {
                rankingTableView.refresh();
            });
        }
    };

    @FXML
    private Button returnMainMenu;

    @FXML
    private TableView<RankingData> rankingTableView;
    
    @FXML
    private TableColumn<RankingData, Integer> rankTableColumn;

    @FXML
    private TableColumn<RankingData, String> nameTableColumn;
    
    @FXML
    private TableColumn<RankingData, Integer> totalScoreTableColumn;

    @FXML
    private void returnMainMenu(ActionEvent event) throws IOException {
        App.rankingDataList.removeListener(rankingDataListListener);
        
        root = FXMLLoader.load(getClass().getResource("fxml/MainMenu.fxml"));
        scene = new Scene(root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rankTableColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalScoreTableColumn.setCellValueFactory(new PropertyValueFactory<>("totalScore"));

        rankingTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rankingTableView.setItems(App.rankingDataList);
        App.rankingDataList.addListener(rankingDataListListener);
    }
}
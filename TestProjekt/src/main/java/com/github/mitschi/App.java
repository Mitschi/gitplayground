package com.github.mitschi;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application {

    @FXML protected ChoiceBox choiceBox;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        choiceBox= new ChoiceBox(FXCollections.observableArrayList("1","2","3"));
        List options = choiceBox.getItems();

       choiceBox.setValue("1");
       String pick = choiceBox.getValue().toString();

      // choiceBox.getSelectionModel().selectedIndexProperty().addListener();

    }


    public static void main(String[] args) {
        launch(args);
    }




    @FXML public void onChangeCountClick(ActionEvent actionEvent) {

    }

    @FXML protected void startProgram(ActionEvent event) {
    }

    @FXML protected void cancelProgram(ActionEvent event) {
    }

}

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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

public class App extends Application {
    protected Stage stage;
    protected String logPath;
    @FXML protected TextField textFieldPath;
    @FXML protected TextField textFieldLog;



    @FXML protected ChoiceBox choiceBox;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        logPath = "";
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

    @FXML
    protected void choosePath(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        try{
            if(!file.getName().equals("pom.xml")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "File has to be a pom.xml file!");
                alert.show();
            }else{
                textFieldPath.setText(file.getPath());
            }
        }catch (RuntimeException e){

        }



    }

    @FXML
    protected void chooseLog(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        logPath = file.getPath();
        textFieldLog.setText(logPath);
    }

}

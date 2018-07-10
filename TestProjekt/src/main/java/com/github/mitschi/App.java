package com.github.mitschi;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App extends Application {
    protected Stage stage;
    protected String logPath;
    @FXML protected TextField textFieldPath;
    @FXML protected TextField textFieldLog;



    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        logPath = "";
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    protected void messageOutput(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hello World!");
        alert.show();

    }

    @FXML
    protected void choosePath(ActionEvent event){
        DirectoryChooser fileChooser = new DirectoryChooser();
        File file = fileChooser.showDialog(stage);

        if(!Files.exists(Paths.get((file.getPath()+"\\pom.xml")))){
            Alert alert = new Alert(Alert.AlertType.ERROR, "pom.xml could not be found!");
            alert.show();
        }

        textFieldPath.setText(file.getPath());
    }

    @FXML
    protected void chooseLog(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        logPath = file.getPath();
        textFieldLog.setText(logPath);
    }

}

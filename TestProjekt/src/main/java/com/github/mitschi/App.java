package com.github.mitschi;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;

//import at.aau.building.BuildLog;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.Properties;

public class App extends Application {

    protected Stage stage;
    protected static String logPath;
    protected static String pomFile;
    protected static int maxSteps;

    @FXML
    protected TextField textFieldPath;
    @FXML
    protected TextField textFieldLog;
    @FXML
    protected ChoiceBox choiceBox;
    @FXML
    protected Label lblPath;
    @FXML
    protected Label lblStrategy;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        logPath = "";
        maxSteps = 1;
        pomFile = "";

        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        primaryStage.setTitle("BuildMedic");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                saveProperties();
            }
        }));
    }


    @FXML
    protected void startProgram(ActionEvent event) {

        if (pomFile.equals("")) {
            lblPath.setTextFill(Color.web("#FF0000"));
        }else
            lblPath.setTextFill(Color.web("#000000"));


    }

    @FXML
    protected void cancelProgram(ActionEvent event) {
    }

    public void initialize() {

        choiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
        List options = choiceBox.getItems();

        choiceBox.setValue("1");
        String pick = choiceBox.getValue().toString();
        maxSteps = Integer.parseInt(pick);
    }


    @FXML
    protected void choosePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(stage);
        try {
            if (!file.getName().equals("pom.xml")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "File has to be a pom.xml file!");
                alert.show();
            } else {
                textFieldPath.setText(file.getPath());
                pomFile = file.getPath();
            }
        } catch (RuntimeException e) {

        }


    }

    @FXML
    protected void chooseLog(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        logPath = file.getPath();
        textFieldLog.setText(logPath);
    }

    private static void saveProperties() {
        Properties properties = new Properties();
        properties.setProperty("logPath", logPath);
        properties.setProperty("pomFile", pomFile);
        properties.setProperty("max_steps", maxSteps+"");
        String savePath = System.getProperty("user.home") + "\\.buildMedic\\config.properties";
        try {
            properties.store(new FileWriter(savePath), "Properties");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

package com.github.mitschi;


import at.aau.FixAction;
import at.aau.Repair;
import at.aau.RepairListener;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;



import javax.swing.table.TableColumn;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

public class App extends Application implements RepairListener {

    protected Stage stage;
    protected static String logPath;
    protected static String pomFile;
    protected static int maxSteps;
    protected Scene scene;
    protected static String savePath;

    protected String revision;


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
    @FXML
    protected TextField textFieldRevision;

    @FXML
    protected Tab detailsTab;

    @FXML
    protected TabPane tapPane;

    @FXML
    protected TableView<TableRow> tableView;
    @FXML
    protected javafx.scene.control.TableColumn step;
    @FXML
    protected javafx.scene.control.TableColumn strategies;

    @FXML
    protected javafx.scene.control.TableColumn buildResult;



    @FXML
    protected CheckBox delete;
    @FXML
    protected CheckBox add;
    @FXML
    protected CheckBox insert;
    @FXML
    protected CheckBox version;


    @Override
    public void start(Stage primaryStage) throws Exception {

        stage = primaryStage; // make an global reference for primaryStage
        logPath = ""; // initializing logPath
        maxSteps = 1; // initializing maxSteps
        pomFile = ""; // initializing pomFile

        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml")); // loading fxml-File
        scene = new Scene(root, 800, 600); // initializing scene

        primaryStage.setTitle("BuildMedic"); // Setting Title of Application
        primaryStage.setResizable(false); // Set Stage to not resizeable
        primaryStage.setScene(scene); // Adding Scene to Stage
        primaryStage.show(); // Showing Stage


    }


    public static void main(String[] args) {
        launch(args); // Launch the Application
        // Saves Properties before the Application terminates
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                saveProperties();
            }
        }));
    }


    @FXML
    protected void startProgram(ActionEvent event) {
        // show detailsTab when the Startbutton is pressed
        tapPane.getTabs().add(detailsTab);
        SingleSelectionModel<Tab> selectionModel = tapPane.getSelectionModel();
        selectionModel.select(detailsTab);

        // Marking missing parameters
        if (pomFile.equals(""))
            lblPath.setTextFill(Color.web("#FF0000"));
        else
            lblPath.setTextFill(Color.web("#000000"));


        if (!delete.isSelected() && !add.isSelected() && !insert.isSelected() && !version.isSelected())
            lblStrategy.setTextFill(Color.web("#FF0000"));
        else
            lblStrategy.setTextFill(Color.web("#000000"));

        // Start the Repairtool
        Repair repair = new Repair();
        repair.addRepairListener(this);

        File repoFile = new File(pomFile); // Load File
        getRevision(); // Load Revision-String

        //Load maxSteps from ChoiceBox
        String pick = choiceBox.getValue().toString();
        maxSteps = Integer.parseInt(pick);

//        try {
//            repair.repair(repoFile,revision,maxSteps,null, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }




    }

    @FXML
    protected void cancelProgram(ActionEvent event) {

    }


    public void initialize() {
        // Set detailsTab to non-visible in the beginning
        tapPane.getTabs().remove(detailsTab);

        // Setting up ChoiceBox
        choiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
        choiceBox.setValue("1");
        String pick = choiceBox.getValue().toString();
        maxSteps = Integer.parseInt(pick);

        // Setting up TableView
        tableView.setEditable(true);
        //tableView.getColumns().add("123");
        step.setCellValueFactory(new PropertyValueFactory<TableRow, Integer>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<TableRow, String>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<TableRow, String>("buildResult"));

        ObservableList<TableRow> data = FXCollections.observableArrayList(new TableRow(1, "strat1","success"),new TableRow(2, "strat2","failed"));

        tableView.setItems(data);

        // Load existing Properties
        try {
            Properties startProperties = new Properties();
            savePath = System.getProperty("user.home") + "\\.buildMedic"; // initializing savePath
            startProperties.load(new FileReader(savePath + "\\config.properties"));
            logPath = startProperties.getProperty("logPath");
            pomFile = startProperties.getProperty("pomFile");
            maxSteps = Integer.parseInt(startProperties.getProperty("max_steps"));

            textFieldLog.setText(logPath);
            textFieldPath.setText(pomFile);

        } catch (Exception e) {

        }
    }


    @FXML
    protected void choosePath(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();

        // Open FileChooser and wait for Input
        File file = fileChooser.showOpenDialog(stage);

        try {
            // Check if input is an pom.xml file otherwise open an alert
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
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        // Open FileChooser
        File file = fileChooser.showOpenDialog(stage);
        // Load Path to TextField
        logPath = file.getPath();
        textFieldLog.setText(logPath);
    }

    private static void saveProperties() {
        // Initialize Properties
        Properties properties = new Properties();
        properties.setProperty("logPath", logPath);
        properties.setProperty("pomFile", pomFile);
        properties.setProperty("max_steps", maxSteps + "");

        try {
            // write properties to C:\Users\%USERPROFILE%\.buildMedic\config.properties if non-existing Path will be created
            if (!Files.exists(Paths.get(savePath))) {
                new File(savePath).mkdirs();
                properties.store(new FileWriter(savePath + "\\config.properties"), "Properties");

            } else {
                properties.store(new FileWriter(savePath + "\\config.properties"), "Properties");


            }
        } catch (IOException e) {
        }

    }

    protected void getRevision() {
        // Load revision-String
        revision = textFieldRevision.getText();
    }

    @Override
    public void repairStarted(File file, String s, int i, List<Class> list) {

    }

    @Override
    public void repairEnded() {

    }

    @Override
    public void stepStarted(int i, int i1) {

    }

    @Override
    public void stepEnded(int i, int i1) {

    }

    @Override
    public void repairFound(List<FixAction> list) {

    }

    @Override
    public void printText(String s) {

    }
}

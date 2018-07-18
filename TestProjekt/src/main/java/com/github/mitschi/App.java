package com.github.mitschi;


import at.aau.FixAction;
import at.aau.fixStrategies.*;
import at.aau.Repair;
import at.aau.RepairListener;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import scala.util.parsing.combinator.testing.Str;

import javax.swing.table.TableColumn;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class App extends Application implements RepairListener {

    protected Stage stage;
    protected static String logPath;
    protected static String pomFile;
    protected static int maxSteps;
    protected Scene scene;
    protected static String savePath;
    protected static String revision;
    protected ArrayList<Process> processList;
    protected int processCounter;


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
    protected Label lblLog;
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

    @FXML
    protected ListView<Process> listView;


    public void initialize() {
        // Set detailsTab to non-visible in the beginning
        tapPane.getTabs().remove(detailsTab);
        tapPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        processList = new ArrayList<Process>();
        //processList.add(new Process(null, detailsTab));
        processCounter = 0;

        // Setting up ChoiceBox
        choiceBox.setItems(FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15", "16", "17", "18", "19", "20"));
        choiceBox.setValue("1");
        String pick = choiceBox.getValue().toString();
        maxSteps = Integer.parseInt(pick);
        // Setting up TableView
        tableView.setEditable(true);
        step.setCellValueFactory(new PropertyValueFactory<TableRow, Integer>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<TableRow, String>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<TableRow, String>("buildResult"));

        ObservableList<TableRow> data = FXCollections.observableArrayList(new TableRow(1, "strat1", "success"), new TableRow(2, "strat2", "failed"));

        tableView.setItems(data);

        // Load existing Properties
        try {
            Properties startProperties = new Properties();
            savePath = System.getProperty("user.home") + "\\.buildMedic"; // initializing savePath
            startProperties.load(new FileReader(savePath + "\\config.properties"));
            logPath = startProperties.getProperty("logPath");
            pomFile = startProperties.getProperty("pomFile");
            maxSteps = Integer.parseInt(startProperties.getProperty("max_steps"));
            choiceBox.setValue(maxSteps + "");
            textFieldLog.setText(logPath);
            textFieldPath.setText(pomFile);

        } catch (Exception e) {

        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        logPath = ""; // initializing logPath
        maxSteps = 1; // initializing maxSteps
        pomFile = ""; // initializing pomFile

        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml")); // loading fxml-File
        scene = new Scene(root, 800, 600); // initializing scene
        primaryStage.setTitle("BuildMedic"); // Setting Title of Application
        primaryStage.setResizable(false); // Set Stage to not resizeable
        primaryStage.setScene(scene); // Adding Scene to Stage
        primaryStage.show(); // Showing Stage
        stage = primaryStage; // make a global reference for primaryStage
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


        boolean isPom = testForPom();
        boolean isSelected = true;
        boolean isTxt = testForTxt();
        List<Class> allowedStrats = new ArrayList<Class>();

        if (add.isSelected())
            allowedStrats.add(AddRepositoryEntryAction.class);

        if (delete.isSelected())
            allowedStrats.add(DeleteDependencyAction.class);

        if (insert.isSelected())
            allowedStrats.add(InsertDependencyAction.class);

        if (version.isSelected())
            allowedStrats.add(VersionUpdateAction.class);

        // Marking missing parameters
        if (pomFile.equals("") || !isPom)
            lblPath.setTextFill(Color.web("#FF0000"));
        else
            lblPath.setTextFill(Color.web("#000000"));

        if (!isTxt)
            lblLog.setTextFill(Color.web("#FF0000"));
        else
            lblLog.setTextFill(Color.web("#000000"));


        if (!delete.isSelected() && !add.isSelected() && !insert.isSelected() && !version.isSelected()) {
            lblStrategy.setTextFill(Color.web("#FF0000"));
            isSelected = false;
        } else
            lblStrategy.setTextFill(Color.web("#000000"));

        if (pomFile.isEmpty() || !isSelected) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Missing Fields!");
            alert.show();
        } else {
            Process process = new Process(pomFile);
            boolean isAlreadyRunning = false;
            for (Process p : processList) {
                if (p.equals(process))
                    isAlreadyRunning = true;
            }
            if (!isAlreadyRunning) {
                processList.add(process);
                processCounter++;
                process.getProcessTab().setClosable(false);
                tapPane.getTabs().add(processList.get(processCounter - 1).getProcessTab());
                listView.getItems().add(process);

                File repoFile = new File(pomFile); // Load File
                getRevision(); // Load Revision-String

                //Load maxSteps from ChoiceBox
                String pick = choiceBox.getValue().toString();
                maxSteps = Integer.parseInt(pick);

                try {
                    //process.getRepair().repair(repoFile, revision, maxSteps, null, allowedStrats);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to start Repairtool!");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Process already running!");
                alert.show();
            }
        }

    }

    @FXML
    protected void cancelProgram(ActionEvent event) {
        ObservableList<Process> selectedItems = listView.getSelectionModel().getSelectedItems();

        Alert alert =
                new Alert(Alert.AlertType.WARNING,
                        "Do you want to cancel the process?",
                        ButtonType.YES,
                        ButtonType.NO);

        Optional<ButtonType> decision = alert.showAndWait();
        if(decision.get() == ButtonType.YES){
            for(int j = 0; j < selectedItems.size(); j++){
                Process selP = selectedItems.get(j);
                for(int i = 0; i < processList.size(); i++){
                    Process p = processList.get(i);
                    if(p.equals(selP)){
                        processList.remove(p);
                        listView.getItems().remove(p);
                        processCounter--;
                        p.getProcessTab().setClosable(true);
                    }
                }
            }
        }

    }


    @FXML
    protected void choosePath(ActionEvent event) {
        // Initialize FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose pom.xml");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Extensible Markup Language", "*.xml"));
        // Open FileChooser and wait for Input
        File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());

        try {
            // Check if input is a pom.xml file otherwise open an alert
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
        fileChooser.setTitle("Choose Logfile");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Textfile", "*.txt"));
        try {
            // Open FileChooser
            File file = fileChooser.showOpenDialog(lblPath.getScene().getWindow());
            // Load Path to TextField
            logPath = file.getPath();
            textFieldLog.setText(logPath);
        } catch (Exception e) {

        }

    }

    private static void saveProperties() {
        // Initialize Properties
        Properties properties = new Properties();
        properties.setProperty("logPath", logPath);
        properties.setProperty("pomFile", pomFile);
        properties.setProperty("max_steps", maxSteps + "");

        try {
            // write properties to C:\Users\%USERPROFILE%\.buildMedic\config.properties if non-existing, Path will be created
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
        // Select detailsTab
        SingleSelectionModel<Tab> selectionModel = tapPane.getSelectionModel();
        selectionModel.select(processList.get(0).getProcessTab());

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

    protected boolean testForPom() {
        String s = textFieldPath.getText();
        try {
            File file = new File(s);

            if (!file.getName().equals("pom.xml")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Path has to be a pom.xml file!");
                alert.show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a file!");
            alert.show();

            return false;
        }
    }

    protected boolean testForTxt() {
        String s = textFieldLog.getText();
        try {
            File file = new File(s);

            if (!file.getName().contains(".txt")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "LogFile has to be a .txt file!");
                alert.show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This is not a file!");
            alert.show();

            return false;
        }
    }

    @FXML
    protected void contextMenuListView(ActionEvent event){
        if(listView.getItems().size() != 0){
            MultipleSelectionModel<Process> sel = listView.getSelectionModel();
            Process p = sel.getSelectedItem();
            SingleSelectionModel<Tab> selectionModel = tapPane.getSelectionModel();
            if(!sel.isEmpty())
                selectionModel.select(p.getProcessTab());
        }
    }


}
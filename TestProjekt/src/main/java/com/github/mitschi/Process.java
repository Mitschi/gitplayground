package com.github.mitschi;

import at.aau.FixAction;
import at.aau.RepairListener;
import at.aau.Repair;
import at.aau.building.BuildLog;
import at.aau.entity.BuildResult;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.reactfx.util.FxTimer;

import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class Process implements RepairListener{
    private String filePath;
    private Tab processTab;
    private TableView<TableRow> table;
    private ProgressBar progressBar;
    private Label label;
    private Repair repair;
    private double progress;
    private BooleanProperty isRunning;
    private Stage stage;
    private String logBuffer;

    private javafx.scene.control.TableColumn step;

    private javafx.scene.control.TableColumn strategies;

    private javafx.scene.control.TableColumn buildResult;

    private javafx.scene.control.TableColumn button;



    public Repair getRepair() {
        return repair;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Process(String filePath, Stage stage){
        isRunning = new SimpleBooleanProperty(false);
        this.stage = stage;
        repair = new Repair();
        repair.addRepairListener(this);
        this.filePath = filePath; // Set Filepath
        progressBar = new ProgressBar(); // initialize progressBar
        table = new TableView<TableRow>(); // initialize Table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.processTab = new Tab();// initialize Tab
        processTab.setText(filePath); // set Title to Filepath
        processTab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                App.tabListener.tabOnClose();
            }
        });
        // Set Table Columns
        step = new TableColumn();
        strategies = new TableColumn();
        buildResult = new TableColumn();
        button = new TableColumn();
        step.setText("Steps");
        strategies.setText("Strategies");
        buildResult.setText("Build Result");
        button.setText("Log Windows");
        // add table columns
        table.getColumns().addAll(step, strategies, buildResult, button);

        step.setCellValueFactory(new PropertyValueFactory<>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<>("result"));
        button.setCellValueFactory(new PropertyValueFactory<>("showLog"));

        label = new Label("");// Add progressLabel
        Pane pane = new Pane(); // Add pane as container
        table.setPrefSize(800, 540); // Set size of Table
        // Translate progressBar and label
        progressBar.setPrefWidth(800);
        progressBar.setTranslateY(547);

        label.setTranslateY(547);
        label.setTranslateX(400);
        processTab.setContent(pane); // add pane to tab
        pane.getChildren().addAll(progressBar, table, label); // add contents to pane



    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Tab getProcessTab() {
        return processTab;
    }

    public void setProcessTab(Tab processTab) {
        this.processTab = processTab;
    }

    public boolean equals(Process o) {
        return this.getFilePath().equals(o.getFilePath());
    }

    @Override
    public int hashCode() {

        return Objects.hash(filePath, processTab);
    }

    public void addData(ObservableList list){
        // Add data to table
        table.setItems(list);
    }

    @Override
    public void repairStarted(File file, String s, int i, List<Class> list) {

    }

    @Override
    public void repairEnded() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                App.progressListener.progressFinished(Process.this);
                processTab.setClosable(true);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Repair finished!");
                alert.setTitle(filePath);
                alert.show();
            }
        });
    }

    @Override
    public void stepStarted(int i, int i1, BuildLog buildLog) {

    }

    @Override
    public void stepEnded(int i, int i1, BuildLog buildLog) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Update progressBar and progressLbl
                double progress = (double) i / (double) i1;
                progressBar.setProgress(progress);
                progress *= 100.0;
                label.setText(String.format("%.2f", progress) + "%");

                //TableRow(int step, String strategie, String buildResult, Stage stage, String filePath)

                ObservableList<TableRow> data = FXCollections.observableArrayList(new TableRow(i, null, buildLog, stage, filePath));
                addData(data);

                table.getItems().get(0).getLogWindow().area.appendText(logBuffer+"\n");
                logBuffer = "";
            }
        });
    }


    @Override
    public void repairFound(List<FixAction> list) {

    }

    @Override
    public void printText(String s) {

    }

    @Override
    public void newBuildLine(String line) {
        logBuffer += line + "\n";
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//
//                table.getItems().get(0).getLogWindow().area.appendText(line+"\n");
//            }
//        });
    }

    @Override
    public String toString() {
        return filePath;
    }


    public void start(File repoFolder, String revision, int max_steps, List<Class> allowedStrategies){

//repair.repair(repoFolder, revision, max_steps,"statistic", allowedStrategies);
        new Thread(){
            @Override
            public void run() {
                try {
                    isRunning.setValue(true);
                    if(repair == null){

                    }else
                        repair.repair(repoFolder, revision, max_steps,"statistic", allowedStrategies);
                } catch (FileNotFoundException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}

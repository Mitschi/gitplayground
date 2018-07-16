package com.github.mitschi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class Process {
    private String filePath;
    private Tab processTab;
    private TableView<TableRow> table;
    private ProgressBar progressBar;
    private Label label;

    private javafx.scene.control.TableColumn step;

    private javafx.scene.control.TableColumn strategies;

    private javafx.scene.control.TableColumn buildResult;

    public Process(String filePath){
        this.filePath = filePath; // Set Filepath
        progressBar = new ProgressBar(); // initialize progressBar
        table = new TableView<TableRow>(); // initialize Table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.processTab = new Tab();// initialize Tab
        processTab.setText(filePath); // set Title to Filepath
        // Set Table Columns
        step = new TableColumn();
        strategies = new TableColumn();
        buildResult = new TableColumn();
        step.setText("Steps");
        strategies.setText("Strategies");
        buildResult.setText("Build Result");
        step.setCellValueFactory(new PropertyValueFactory<TableRow, Integer>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<TableRow, String>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<TableRow, String>("buildResult"));

        label = new Label("");// Add progressLabel
        Pane pane = new Pane(); // Add pane as container
        table.setPrefSize(800, 540); // Set size of Table
        // Translate progressBar and label
        progressBar.setPrefWidth(800);
        progressBar.setTranslateY(547);
        progressBar.setProgress(0.5);
        label.setTranslateY(547);
        label.setTranslateX(400);
        label.setText("50%");
        processTab.setContent(pane); // add pane to tab
        pane.getChildren().addAll(progressBar, table, label); // add contents to pane

        // add table columns
        table.getColumns().addAll(step, strategies, buildResult);

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
}

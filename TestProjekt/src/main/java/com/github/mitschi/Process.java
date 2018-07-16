package com.github.mitschi;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class Process {
    private String filePath;
    private Tab processTab;
    private TableView<TableRow> table;
    private ProgressBar progressBar;

    private javafx.scene.control.TableColumn step;

    private javafx.scene.control.TableColumn strategies;

    private javafx.scene.control.TableColumn buildResult;

    public Process(String filePath){
        this.filePath = filePath;
        progressBar = new ProgressBar();
        table = new TableView<TableRow>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.processTab = new Tab();
        processTab.setText(filePath);
        step = new TableColumn();
        strategies = new TableColumn();
        buildResult = new TableColumn();
        Pane pane = new Pane();
        pane.getChildren().addAll(table, progressBar);
        processTab.setContent(pane);

        step.setText("Steps");
        strategies.setText("Strategies");
        buildResult.setText("Build Result");

        step.setCellValueFactory(new PropertyValueFactory<TableRow, Integer>("step"));
        strategies.setCellValueFactory(new PropertyValueFactory<TableRow, String>("strategie"));
        buildResult.setCellValueFactory(new PropertyValueFactory<TableRow, String>("buildResult"));

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
        table.setItems(list);
    }
}

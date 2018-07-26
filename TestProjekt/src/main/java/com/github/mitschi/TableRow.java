package com.github.mitschi;

import at.aau.building.BuildLog;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;

import java.util.Objects;

public class TableRow {
    private int step;
    private String strategie;
    private BuildLog buildResult;



    private String result;
    private Button showLog;
    private Stage stage;
    private String filePath;
    private LogWindow logWindow;

    public LogWindow getLogWindow() {
        return logWindow;
    }

    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    public TableRow(int step, String strategie, BuildLog buildResult, Stage stage, String filePath) {
        // initialize Fields
        this.step = step;
        this.strategie = strategie;
        this.buildResult = buildResult;
        this.showLog = new Button("Show Log");
        this.result = buildResult.getBuildResult().toString();
        this.stage = stage;
        this.filePath = filePath;
        this.logWindow = new LogWindow(stage);
        // set event handler for button
        this.showLog.setOnAction(
                event -> {
                    // open new LogWindow
                    logWindow.showDialog(filePath, step+"", buildResult);

                }

        );
    }

    public TableRow(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableRow tableRow = (TableRow) o;
        return step == tableRow.step &&
                Objects.equals(strategie, tableRow.strategie) &&
                Objects.equals(buildResult, tableRow.buildResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(step, strategie, buildResult);
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getStrategie() {
        return strategie;
    }

    public void setStrategie(String strategie) {
        this.strategie = strategie;
    }

    public BuildLog getBuildResult() {
        return buildResult;
    }

    public void setBuildResult(BuildLog buildResult) {
        this.buildResult = buildResult;
    }

    public Button getShowLog() {
        return showLog;
    }

    public void setShowLog(Button showLog) {
        this.showLog = showLog;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

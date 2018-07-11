package com.github.mitschi;

import java.util.Objects;

public class TableRow {
    private int step;
    private String strategie;
    private String buildResult;

    public TableRow(int step, String strategie, String buildResult) {
        this.step = step;
        this.strategie = strategie;
        this.buildResult = buildResult;
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

    public String getBuildResult() {
        return buildResult;
    }

    public void setBuildResult(String buildResult) {
        this.buildResult = buildResult;
    }
}

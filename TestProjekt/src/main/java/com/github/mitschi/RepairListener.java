package com.github.mitschi;

public interface RepairListener {
    public void repairStarted();
    public void repairEnded();
    public void stepPerformed();
    public void repairFound();
}

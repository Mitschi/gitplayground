package com.github.mitschi;

public interface ProgressListener {
    public void changeProgress(Process process);
    public void progressFinished(Process process);
}

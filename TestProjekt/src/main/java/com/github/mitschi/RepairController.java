package com.github.mitschi;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.List;

public class RepairController {
    public void repair(File repoFolder, String revision, int MAX_STEPS, String statisticId, List<Class> allowedStrategies, RepairListener repairListener) throws FileNotFoundException, ParseException {
        //Currently, this method fakes a repair action

        repairListener.repairStarted();
        for(int i=0;i<=MAX_STEPS;i++) {
            repairListener.stepPerformed();
            try {
                Thread.sleep((long) (Math.random()*1500));
            } catch (InterruptedException e) {}
        }
        repairListener.repairFound();
        repairListener.repairEnded();
    }
}

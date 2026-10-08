package com.codemorph.backend.model.roadmap;

import java.util.ArrayList;
import java.util.List;

public class MigrationWave {

    private int waveNumber;

    private List<MigrationStep> components =
            new ArrayList<>();

    private double averageImpact;
    private double averageRisk;

    private String objective;

    public MigrationWave() {
    }

    public MigrationWave(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public List<MigrationStep> getComponents() {
        return components;
    }

    public void setComponents(
            List<MigrationStep> components) {

        this.components = components;
    }

    public double getAverageImpact() {
        return averageImpact;
    }

    public void setAverageImpact(double averageImpact) {
        this.averageImpact = averageImpact;
    }

    public double getAverageRisk() {
        return averageRisk;
    }

    public void setAverageRisk(double averageRisk) {
        this.averageRisk = averageRisk;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }
}
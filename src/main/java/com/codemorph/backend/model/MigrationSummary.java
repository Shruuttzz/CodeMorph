package com.codemorph.backend.model;

public class MigrationSummary {

    private int totalComponents;
    private int highRiskComponents;
    private int mediumRiskComponents;
    private int lowRiskComponents;

    private double averageRisk;
    private double highestRisk;

    public int getTotalComponents() {
        return totalComponents;
    }

    public void setTotalComponents(int totalComponents) {
        this.totalComponents = totalComponents;
    }

    public int getHighRiskComponents() {
        return highRiskComponents;
    }

    public void setHighRiskComponents(int highRiskComponents) {
        this.highRiskComponents = highRiskComponents;
    }

    public int getMediumRiskComponents() {
        return mediumRiskComponents;
    }

    public void setMediumRiskComponents(int mediumRiskComponents) {
        this.mediumRiskComponents = mediumRiskComponents;
    }

    public int getLowRiskComponents() {
        return lowRiskComponents;
    }

    public void setLowRiskComponents(int lowRiskComponents) {
        this.lowRiskComponents = lowRiskComponents;
    }

    public double getAverageRisk() {
        return averageRisk;
    }

    public void setAverageRisk(double averageRisk) {
        this.averageRisk = averageRisk;
    }

    public double getHighestRisk() {
        return highestRisk;
    }

    public void setHighestRisk(double highestRisk) {
        this.highestRisk = highestRisk;
    }
}

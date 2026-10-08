package com.codemorph.backend.model.roadmap;

import java.util.ArrayList;
import java.util.List;

public class MigrationRoadmap {

    private int totalWaves;

    private List<MigrationWave> waves =
            new ArrayList<>();

    private List<String> blockedComponents =
            new ArrayList<>();

    private List<List<String>> dependencyCycles =
            new ArrayList<>();

    public MigrationRoadmap() {
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public void setTotalWaves(int totalWaves) {
        this.totalWaves = totalWaves;
    }

    public List<MigrationWave> getWaves() {
        return waves;
    }

    public void setWaves(List<MigrationWave> waves) {
        this.waves = waves;
    }

    public List<String> getBlockedComponents() {
        return blockedComponents;
    }

    public void setBlockedComponents(
            List<String> blockedComponents) {

        this.blockedComponents = blockedComponents;
    }

    public List<List<String>> getDependencyCycles() {
        return dependencyCycles;
    }

    public void setDependencyCycles(
            List<List<String>> dependencyCycles) {

        this.dependencyCycles = dependencyCycles;
    }
}
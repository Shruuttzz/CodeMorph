package com.codemorph.backend.model;

import java.util.List;
import java.util.Map;

public class ProjectSummary {

    private String projectName;
    private int javaFileCount;
    private List<Map<String, Object>> astAnalysis;

    public ProjectSummary() {
    }

    public ProjectSummary(String projectName, int javaFileCount) {
        this.projectName = projectName;
        this.javaFileCount = javaFileCount;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getJavaFileCount() {
        return javaFileCount;
    }

    public void setJavaFileCount(int javaFileCount) {
        this.javaFileCount = javaFileCount;
    }

    public List<Map<String, Object>> getAstAnalysis() {
        return astAnalysis;
    }

    public void setAstAnalysis(List<Map<String, Object>> astAnalysis) {
        this.astAnalysis = astAnalysis;
    }
}
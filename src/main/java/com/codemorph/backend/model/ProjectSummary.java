package com.codemorph.backend.model;

public class ProjectSummary {

    private String projectName;
    private int javaFileCount;

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
}
package com.codemorph.backend.model;

public class ComponentAnalysis {

    private String className;
    private String packageName;
    private String sourceFile;

    // Complexity
    private int loc;
    private int methodCount;
    private int fieldCount;
    private int branchCount;
    private double cyclomaticComplexity;
    private int inheritanceDepth;

    // Migration evidence
    private int deprecatedApiCount;
    private double migrationIssueScore;

    // Graph metrics
    private int fanIn;
    private int fanOut;
    private int blastRadius;
    private double centrality;

    // Normalized scores
    private double complexityScore;

    // Final scores
    private double difficultyScore;
    private double impactScore;
    private double riskScore;

    public ComponentAnalysis() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public void setMethodCount(int methodCount) {
        this.methodCount = methodCount;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getBranchCount() {
        return branchCount;
    }

    public void setBranchCount(int branchCount) {
        this.branchCount = branchCount;
    }

    public double getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(double cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public int getInheritanceDepth() {
        return inheritanceDepth;
    }

    public void setInheritanceDepth(int inheritanceDepth) {
        this.inheritanceDepth = inheritanceDepth;
    }

    public int getDeprecatedApiCount() {
        return deprecatedApiCount;
    }

    public void setDeprecatedApiCount(int deprecatedApiCount) {
        this.deprecatedApiCount = deprecatedApiCount;
    }

    public double getMigrationIssueScore() {
        return migrationIssueScore;
    }

    public void setMigrationIssueScore(double migrationIssueScore) {
        this.migrationIssueScore = migrationIssueScore;
    }

    public int getFanIn() {
        return fanIn;
    }

    public void setFanIn(int fanIn) {
        this.fanIn = fanIn;
    }

    public int getFanOut() {
        return fanOut;
    }

    public void setFanOut(int fanOut) {
        this.fanOut = fanOut;
    }

    public int getBlastRadius() {
        return blastRadius;
    }

    public void setBlastRadius(int blastRadius) {
        this.blastRadius = blastRadius;
    }

    public double getCentrality() {
        return centrality;
    }

    public void setCentrality(double centrality) {
        this.centrality = centrality;
    }

    public double getComplexityScore() {
        return complexityScore;
    }

    public void setComplexityScore(double complexityScore) {
        this.complexityScore = complexityScore;
    }

    public double getDifficultyScore() {
        return difficultyScore;
    }

    public void setDifficultyScore(double difficultyScore) {
        this.difficultyScore = difficultyScore;
    }

    public double getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(double impactScore) {
        this.impactScore = impactScore;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }
}


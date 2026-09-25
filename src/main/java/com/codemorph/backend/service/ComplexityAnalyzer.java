package com.codemorph.backend.service;



import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;

@Service
public class ComplexityAnalyzer {

    public ComponentAnalysis analyze(
            ClassOrInterfaceDeclaration clazz,
            CompilationUnit compilationUnit,
            String sourceFile) {

        ComponentAnalysis analysis =
                new ComponentAnalysis();

        analysis.setClassName(
                clazz.getNameAsString()
        );

        analysis.setPackageName(
                compilationUnit.getPackageDeclaration()
                        .map(pd -> pd.getNameAsString())
                        .orElse("")
        );

        analysis.setSourceFile(sourceFile);

        int methodCount =
                clazz.getMethods().size();

        int fieldCount =
                clazz.getFields().size();

        int branchCount =
                countBranches(clazz);

        double cyclomatic =
                calculateCyclomaticComplexity(clazz);

        int inheritanceDepth =
                calculateInheritanceDepth(clazz);

        int loc =
                calculateLoc(
                        clazz,
                        compilationUnit
                );

        analysis.setMethodCount(methodCount);
        analysis.setFieldCount(fieldCount);
        analysis.setBranchCount(branchCount);
        analysis.setCyclomaticComplexity(cyclomatic);
        analysis.setInheritanceDepth(inheritanceDepth);
        analysis.setLoc(loc);

        return analysis;
    }

    private int calculateLoc(
            ClassOrInterfaceDeclaration clazz,
            CompilationUnit compilationUnit) {

        if (clazz.getRange().isEmpty()) {
            return 0;
        }

        var range = clazz.getRange().get();

        return range.end.line - range.begin.line + 1;
    }

    private int countBranches(
            ClassOrInterfaceDeclaration clazz) {

        int count = 0;

        count += clazz.findAll(IfStmt.class).size();
        count += clazz.findAll(ForStmt.class).size();
        count += clazz.findAll(ForEachStmt.class).size();
        count += clazz.findAll(WhileStmt.class).size();
        count += clazz.findAll(CatchClause.class).size();
        count += clazz.findAll(ConditionalExpr.class).size();

        return count;
    }

    private double calculateCyclomaticComplexity(
            ClassOrInterfaceDeclaration clazz) {

        /*
         * Basic McCabe-style approximation:
         *
         * Complexity = 1 + number of decision points
         *
         * Decision points:
         * if
         * for
         * foreach
         * while
         * catch
         * ternary
         *
         * This is intentionally a first implementation.
         */

        return 1.0 + countBranches(clazz);
    }

    private int calculateInheritanceDepth(
            ClassOrInterfaceDeclaration clazz) {

        /*
         * At this stage JavaParser may only give us
         * the immediate superclass name.
         *
         * Therefore:
         *
         * no extends → 0
         * extends another class → 1
         *
         * Full repository-level inheritance depth can
         * be implemented later using the class index.
         */

        return clazz.getExtendedTypes().isEmpty()
                ? 0
                : 1;
    }
}
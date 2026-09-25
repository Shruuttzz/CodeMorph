package com.codemorph.backend.model;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ParsedClass {

    private final String className;
    private final String sourceFile;
    private final CompilationUnit compilationUnit;
    private final ClassOrInterfaceDeclaration declaration;

    public ParsedClass(
            String className,
            String sourceFile,
            CompilationUnit compilationUnit,
            ClassOrInterfaceDeclaration declaration) {

        this.className = className;
        this.sourceFile = sourceFile;
        this.compilationUnit = compilationUnit;
        this.declaration = declaration;
    }

    public String getClassName() {
        return className;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public ClassOrInterfaceDeclaration getDeclaration() {
        return declaration;
    }
}

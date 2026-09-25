package com.codemorph.backend.service;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AstService {

    public List<Map<String, Object>> analyzeRepository(Path repoRoot) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(repoRoot)) {
            List<Path> javaFiles = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
            for (Path file : javaFiles) {
                results.add(analyzeFile(file));
            }
        }
        return results;
    }

    private Map<String, Object> analyzeFile(Path file) {
        Map<String, Object> fileInfo = new LinkedHashMap<>();
        fileInfo.put("file", file.getFileName().toString());

        try {
            CompilationUnit cu = StaticJavaParser.parse(file);

            List<String> classNames = new ArrayList<>();
            List<String> methodNames = new ArrayList<>();
            List<String> deprecatedFound = new ArrayList<>();

            cu.findAll(ClassOrInterfaceDeclaration.class)
                    .forEach(c -> classNames.add(c.getNameAsString()));

            cu.findAll(MethodDeclaration.class)
                    .forEach(m -> methodNames.add(m.getNameAsString()));

            cu.getImports().forEach(imp -> {
                String importName = imp.getNameAsString();

                if (isActuallyDeprecated(importName)) {
                    deprecatedFound.add(importName);
                }
            });

            fileInfo.put("classes", classNames);
            fileInfo.put("methods", methodNames);
            fileInfo.put("deprecatedImports", deprecatedFound);
            fileInfo.put("status", "parsed");

        } catch (Exception e) {
            fileInfo.put("status", "failed");
            fileInfo.put("error", "Could not parse file — check for syntax errors near the reported location.");
        }
        return fileInfo;
    }

    /**
     * Checks if a class is genuinely marked @Deprecated by the JDK itself,
     * using reflection instead of a hardcoded list.
     */
    private boolean isActuallyDeprecated(String fullyQualifiedName) {
        try {
            Class<?> clazz = Class.forName(fullyQualifiedName);
            return clazz.isAnnotationPresent(Deprecated.class);
        } catch (ClassNotFoundException e) {
            // Not a JDK class, or it's a project-internal class not on this
            // classpath (e.g. com.example.legacy.UserAccount) — can't check, skip it
            return false;
        } catch (Throwable e) {
            // Defensive: some classes throw on load (e.g. missing native libs)
            return false;
        }
    }
}
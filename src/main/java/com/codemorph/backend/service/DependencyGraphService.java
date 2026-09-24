package com.codemorph.backend.service;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Service
public class DependencyGraphService {

    /**
     * Builds a class-level dependency graph for the repository.
     * Node = a class/interface declared in the repo.
     * Edge = "this class references that class" (field type, method
     * parameter/return type, object creation, inheritance/implementation).
     */
    public Map<String, Object> buildGraph(Path repoRoot) throws IOException {

        List<Path> javaFiles;
        try (Stream<Path> walk = Files.walk(repoRoot)) {
            javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
        }

        // Pass 1: parse everything once, collect every class/interface name
        // declared anywhere in this repo (so we only draw edges to OUR classes,
        // not to unrelated JDK types like String, List, etc.)
        Set<String> knownClasses = new HashSet<>();
        List<CompilationUnit> parsedUnits = new ArrayList<>();

        for (Path file : javaFiles) {
            try {
                CompilationUnit cu = StaticJavaParser.parse(file);
                parsedUnits.add(cu);
                cu.findAll(ClassOrInterfaceDeclaration.class)
                        .forEach(c -> knownClasses.add(c.getNameAsString()));
            } catch (Exception ignored) {
                // Files that fail to parse are already reported by AstService;
                // just skip them here rather than duplicating that error.
            }
        }

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        knownClasses.forEach(graph::addVertex);

        // Pass 2: for each class, find every reference to another known class
        for (CompilationUnit cu : parsedUnits) {
            for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                String fromClass = cls.getNameAsString();
                Set<String> referenced = new HashSet<>();

                // Field types, e.g. "private BookRepository repo;"
                cls.findAll(FieldDeclaration.class)
                        .forEach(f -> referenced.add(stripGenerics(f.getElementType().asString())));

                // Method return types and parameter types
                cls.findAll(MethodDeclaration.class).forEach(m -> {
                    referenced.add(stripGenerics(m.getTypeAsString()));
                    m.getParameters().forEach(p -> referenced.add(stripGenerics(p.getTypeAsString())));
                });

                // "new X(...)" object creation
                cls.findAll(ObjectCreationExpr.class)
                        .forEach(o -> referenced.add(o.getType().getNameAsString()));

                // Inheritance / interface implementation
                cls.getExtendedTypes().forEach(t -> referenced.add(t.getNameAsString()));
                cls.getImplementedTypes().forEach(t -> referenced.add(t.getNameAsString()));

                for (String ref : referenced) {
                    if (knownClasses.contains(ref) && !ref.equals(fromClass)) {
                        graph.addEdge(fromClass, ref);
                    }
                }
            }
        }

        return toJsonShape(graph);
    }

    private String stripGenerics(String typeName) {
        // "List<Book>" -> "List" ; keeps the check simple for common cases
        int idx = typeName.indexOf('<');
        return idx == -1 ? typeName : typeName.substring(0, idx);
    }

    private Map<String, Object> toJsonShape(Graph<String, DefaultEdge> graph) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (String v : graph.vertexSet()) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", v);
            node.put("label", v);
            nodes.add(node);
        }

        List<Map<String, Object>> edges = new ArrayList<>();
        for (DefaultEdge e : graph.edgeSet()) {
            Map<String, Object> edge = new LinkedHashMap<>();
            edge.put("from", graph.getEdgeSource(e));
            edge.put("to", graph.getEdgeTarget(e));
            edges.add(edge);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("nodes", nodes);
        result.put("edges", edges);
        return result;
    }
}
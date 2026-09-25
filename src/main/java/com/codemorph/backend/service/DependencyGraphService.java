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
     *
     * Node = a class/interface declared in the repository.
     *
     * Edge:
     *     A -> B
     *
     * means:
     *     A depends on / references B
     *
     * Dependencies are detected through:
     * - field types
     * - method return types
     * - method parameter types
     * - object creation
     * - inheritance
     * - interface implementation
     */
    public Map<String, Object> buildGraph(Path repoRoot) throws IOException {

        Graph<String, DefaultEdge> graph = buildGraphObject(repoRoot);

        return toJsonShape(graph);
    }

    /**
     * Builds and returns the actual JGraphT graph.
     *
     * This method is used internally by backend analysis features such as:
     * - fan-in
     * - fan-out
     * - blast radius
     * - centrality
     * - migration impact analysis
     *
     * The graph direction is:
     *
     *     dependent -> dependency
     *
     * Example:
     *
     *     PaymentService -> AuthenticationService
     *
     * means PaymentService depends on AuthenticationService.
     */
    public Graph<String, DefaultEdge> buildGraphObject(Path repoRoot) throws IOException {

        List<Path> javaFiles;

        /*
         * Find every Java file inside the repository.
         */
        try (Stream<Path> walk = Files.walk(repoRoot)) {

            javaFiles = walk
                    .filter(p -> p.toString().endsWith(".java"))
                    .toList();
        }

        /*
         * ---------------------------------------------------------
         * PASS 1
         * ---------------------------------------------------------
         *
         * Parse all Java files and collect the names of all
         * classes/interfaces that belong to this repository.
         *
         * This prevents dependencies such as:
         *
         *     String
         *     List
         *     HashMap
         *     Object
         *
         * from becoming graph nodes.
         */
        Set<String> knownClasses = new HashSet<>();

        List<CompilationUnit> parsedUnits = new ArrayList<>();

        for (Path file : javaFiles) {

            try {

                CompilationUnit cu = StaticJavaParser.parse(file);

                parsedUnits.add(cu);

                cu.findAll(ClassOrInterfaceDeclaration.class)
                        .forEach(c ->
                                knownClasses.add(c.getNameAsString())
                        );

            } catch (Exception ignored) {

                /*
                 * Files that cannot be parsed are skipped here.
                 *
                 * AstService already handles/report parse failures,
                 * so we don't duplicate that logic here.
                 */
            }
        }

        /*
         * ---------------------------------------------------------
         * CREATE GRAPH
         * ---------------------------------------------------------
         */
        Graph<String, DefaultEdge> graph =
                new DefaultDirectedGraph<>(DefaultEdge.class);

        /*
         * Add every repository class/interface as a graph node.
         */
        knownClasses.forEach(graph::addVertex);

        /*
         * ---------------------------------------------------------
         * PASS 2
         * ---------------------------------------------------------
         *
         * Find dependencies between repository classes.
         */
        for (CompilationUnit cu : parsedUnits) {

            for (ClassOrInterfaceDeclaration cls :
                    cu.findAll(ClassOrInterfaceDeclaration.class)) {

                String fromClass = cls.getNameAsString();

                /*
                 * Set is used so that the same dependency is added
                 * only once.
                 */
                Set<String> referenced = new HashSet<>();

                /*
                 * -------------------------------------------------
                 * 1. FIELD TYPES
                 * -------------------------------------------------
                 *
                 * Example:
                 *
                 * private BookRepository repository;
                 *
                 * creates:
                 *
                 * CurrentClass -> BookRepository
                 */
                cls.findAll(FieldDeclaration.class)
                        .forEach(field ->
                                referenced.add(
                                        stripGenerics(
                                                field.getElementType().asString()
                                        )
                                )
                        );

                /*
                 * -------------------------------------------------
                 * 2. METHOD RETURN TYPES
                 * -------------------------------------------------
                 *
                 * Example:
                 *
                 * public User getUser()
                 *
                 * creates:
                 *
                 * CurrentClass -> User
                 */
                cls.findAll(MethodDeclaration.class)
                        .forEach(method -> {

                            referenced.add(
                                    stripGenerics(
                                            method.getTypeAsString()
                                    )
                            );

                            /*
                             * -------------------------------------------------
                             * 3. METHOD PARAMETER TYPES
                             * -------------------------------------------------
                             *
                             * Example:
                             *
                             * public void save(User user)
                             *
                             * creates:
                             *
                             * CurrentClass -> User
                             */
                            method.getParameters()
                                    .forEach(parameter ->
                                            referenced.add(
                                                    stripGenerics(
                                                            parameter.getTypeAsString()
                                                    )
                                            )
                                    );
                        });

                /*
                 * -------------------------------------------------
                 * 4. OBJECT CREATION
                 * -------------------------------------------------
                 *
                 * Example:
                 *
                 * new PaymentService()
                 *
                 * creates:
                 *
                 * CurrentClass -> PaymentService
                 */
                cls.findAll(ObjectCreationExpr.class)
                        .forEach(objectCreation ->
                                referenced.add(
                                        objectCreation
                                                .getType()
                                                .getNameAsString()
                                )
                        );

                /*
                 * -------------------------------------------------
                 * 5. INHERITANCE
                 * -------------------------------------------------
                 *
                 * Example:
                 *
                 * class Child extends Parent
                 *
                 * creates:
                 *
                 * Child -> Parent
                 */
                cls.getExtendedTypes()
                        .forEach(type ->
                                referenced.add(
                                        type.getNameAsString()
                                )
                        );

                /*
                 * -------------------------------------------------
                 * 6. INTERFACE IMPLEMENTATION
                 * -------------------------------------------------
                 *
                 * Example:
                 *
                 * class PaymentService
                 *          implements PaymentProcessor
                 *
                 * creates:
                 *
                 * PaymentService -> PaymentProcessor
                 */
                cls.getImplementedTypes()
                        .forEach(type ->
                                referenced.add(
                                        type.getNameAsString()
                                )
                        );

                /*
                 * -------------------------------------------------
                 * ADD EDGES
                 * -------------------------------------------------
                 */
                for (String ref : referenced) {

                    /*
                     * Only create an edge if the referenced class
                     * actually belongs to this repository.
                     *
                     * Also prevent self-dependencies.
                     */
                    if (knownClasses.contains(ref)
                            && !ref.equals(fromClass)) {

                        graph.addEdge(fromClass, ref);
                    }
                }
            }
        }

        /*
         * Return the actual JGraphT graph.
         */
        return graph;
    }

    /**
     * Removes generic information from a type name.
     *
     * Example:
     *
     *     List<Book>
     *
     * becomes:
     *
     *     List
     *
     * This keeps dependency detection simple for the current
     * implementation.
     */
    private String stripGenerics(String typeName) {

        int idx = typeName.indexOf('<');

        return idx == -1
                ? typeName
                : typeName.substring(0, idx);
    }

    /**
     * Converts the JGraphT graph into the JSON structure expected
     * by the frontend.
     *
     * Example:
     *
     * nodes:
     * [
     *     {"id": "PaymentService", "label": "PaymentService"},
     *     {"id": "UserService", "label": "UserService"}
     * ]
     *
     * edges:
     * [
     *     {"from": "PaymentService", "to": "UserService"}
     * ]
     */
    private Map<String, Object> toJsonShape(
            Graph<String, DefaultEdge> graph) {

        List<Map<String, Object>> nodes = new ArrayList<>();

        /*
         * Create JSON nodes.
         */
        for (String vertex : graph.vertexSet()) {

            Map<String, Object> node =
                    new LinkedHashMap<>();

            node.put("id", vertex);
            node.put("label", vertex);

            nodes.add(node);
        }

        List<Map<String, Object>> edges = new ArrayList<>();

        /*
         * Create JSON edges.
         */
        for (DefaultEdge edge : graph.edgeSet()) {

            Map<String, Object> edgeData =
                    new LinkedHashMap<>();

            edgeData.put(
                    "from",
                    graph.getEdgeSource(edge)
            );

            edgeData.put(
                    "to",
                    graph.getEdgeTarget(edge)
            );

            edges.add(edgeData);
        }

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("nodes", nodes);
        result.put("edges", edges);

        return result;
    }
}
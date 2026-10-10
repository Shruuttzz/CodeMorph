package com.codemorph.backend.service;

import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.model.MigrationSummary;
import com.codemorph.backend.model.ProjectSummary;
import com.codemorph.backend.model.roadmap.MigrationRoadmap;
import com.codemorph.backend.service.roadmap.MigrationRoadmapService;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.io.InputStream;



@Service
public class UploadService {

    private final AstService astService;
    private final DependencyGraphService dependencyGraphService;
    private final ComplexityAnalyzer complexityAnalyzer;
    private final GraphMetricsAnalyzer graphMetricsAnalyzer;
    private final CentralityAnalyzer centralityAnalyzer;
    private final MetricNormalizationService normalizationService;
    private final MigrationImpactAnalyzer migrationImpactAnalyzer;
    private final MigrationSummaryService migrationSummaryService;
    private final MigrationRoadmapService migrationRoadmapService;
    private final ZipExtractionService zipExtractionService;

    public UploadService(
            AstService astService,
            DependencyGraphService dependencyGraphService,
            ComplexityAnalyzer complexityAnalyzer,
            GraphMetricsAnalyzer graphMetricsAnalyzer,
            CentralityAnalyzer centralityAnalyzer,
            MetricNormalizationService normalizationService,
            MigrationImpactAnalyzer migrationImpactAnalyzer,
            MigrationSummaryService migrationSummaryService,
            MigrationRoadmapService migrationRoadmapService,
            ZipExtractionService zipExtractionService
    ) {
        this.astService = astService;
        this.dependencyGraphService = dependencyGraphService;
        this.complexityAnalyzer = complexityAnalyzer;
        this.graphMetricsAnalyzer = graphMetricsAnalyzer;
        this.centralityAnalyzer = centralityAnalyzer;
        this.normalizationService = normalizationService;
        this.migrationImpactAnalyzer = migrationImpactAnalyzer;
        this.migrationSummaryService = migrationSummaryService;
        this.migrationRoadmapService = migrationRoadmapService;
        this.zipExtractionService = zipExtractionService;
    }

    public ProjectSummary processZip(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IOException("Please upload a non-empty ZIP file.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null
                || !originalFilename.toLowerCase().endsWith(".zip")) {
            throw new IOException("Invalid file type. Please upload a ZIP file.");
        }

        Path tempDir = Files.createTempDirectory("codemorph-project-");

        /*
         * STEP 1: Extract only relevant project files.
         */
        try (InputStream input = file.getInputStream()) { {
            zipExtractionService.extractRelevantFiles(input, tempDir);
        }

            /*
             * STEP 2: Count Java files.
             */
            int javaFiles;

            try (Stream<Path> paths = Files.walk(tempDir)) {
                javaFiles = (int) paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString()
                                .toLowerCase()
                                .endsWith(".java"))
                        .count();
            }

            if (javaFiles == 0) {
                throw new IOException(
                        "No Java source files were found in the uploaded ZIP. "
                                + "Make sure the ZIP contains your Java project source."
                );
            }

            /*
             * STEP 3: Determine project name.
             */
            String projectName = originalFilename;

            if (projectName.toLowerCase().endsWith(".zip")) {
                projectName = projectName.substring(
                        0,
                        projectName.length() - 4
                );
            }

            /*
             * STEP 4: AST analysis.
             */
            List<Map<String, Object>> astResults =
                    astService.analyzeRepository(tempDir);

            /*
             * STEP 5: Build dependency graph.
             */
            Map<String, Object> graphJson =
                    dependencyGraphService.buildGraph(tempDir);

            Graph<String, DefaultEdge> graph =
                    dependencyGraphService.buildGraphObject(tempDir);

            /*
             * STEP 6: Initialize project summary.
             */
            ProjectSummary summary =
                    new ProjectSummary(projectName, javaFiles);

            summary.setAstAnalysis(astResults);
            summary.setDependencyGraph(graphJson);

            /*
             * STEP 7: Analyze Java classes and interfaces.
             */
            List<ComponentAnalysis> components = new ArrayList<>();

            try (Stream<Path> paths = Files.walk(tempDir)) {

                List<Path> javaPaths = paths
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString()
                                .toLowerCase()
                                .endsWith(".java"))
                        .toList();

                for (Path javaFile : javaPaths) {
                    try {
                        CompilationUnit compilationUnit =
                                StaticJavaParser.parse(javaFile);

                        List<ClassOrInterfaceDeclaration> classes =
                                compilationUnit.findAll(
                                        ClassOrInterfaceDeclaration.class
                                );

                        for (ClassOrInterfaceDeclaration clazz : classes) {

                            String sourceFile = tempDir
                                    .relativize(javaFile)
                                    .toString();

                            ComponentAnalysis analysis =
                                    complexityAnalyzer.analyze(
                                            clazz,
                                            compilationUnit,
                                            sourceFile
                                    );

                            components.add(analysis);
                        }

                    } catch (Exception exception) {
                        /*
                         * Skip individual Java files that cannot be parsed.
                         * One malformed file should not stop the entire
                         * repository analysis.
                         */
                    }
                }
            }

            /*
             * STEP 8: Graph metrics.
             */
            graphMetricsAnalyzer.analyze(graph, components);

            /*
             * STEP 9: Centrality.
             */
            centralityAnalyzer.analyze(graph, components);

            /*
             * STEP 10: Normalize metrics.
             */
            normalizationService.normalize(components);

            /*
             * STEP 11: Calculate migration difficulty, impact and risk.
             */
            migrationImpactAnalyzer.calculate(components);

            /*
             * STEP 12: Calculate migration summary.
             */
            MigrationSummary migrationSummary =
                    migrationSummaryService.calculate(components);

            /*
             * STEP 13: Generate migration roadmap.
             */
            MigrationRoadmap migrationRoadmap =
                    migrationRoadmapService.generateRoadmap(
                            graph,
                            components
                    );

            /*
             * STEP 14: Attach results.
             */
            summary.setComponentAnalyses(components);
            summary.setMigrationSummary(migrationSummary);
            summary.setMigrationRoadmap(migrationRoadmap);

            return summary;

        } finally {
            /*
             * Always clean up the temporary extracted project,
             * including when analysis fails.
             */
            deleteDirectoryRecursively(tempDir);
        }
    }

    private void deleteDirectoryRecursively(Path directory)
            throws IOException {

        if (directory == null || !Files.exists(directory)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(directory)) {
            List<Path> allPaths = paths
                    .sorted((first, second) ->
                            second.compareTo(first))
                    .toList();

            IOException failure = null;

            for (Path path : allPaths) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exception) {
                    if (failure == null) {
                        failure = exception;
                    } else {
                        failure.addSuppressed(exception);
                    }
                }
            }

            if (failure != null) {
                throw failure;
            }
        }
    }
}
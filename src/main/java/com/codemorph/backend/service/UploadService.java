package com.codemorph.backend.service;

import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.model.MigrationSummary;
import com.codemorph.backend.model.ProjectSummary;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    public UploadService(
            AstService astService,
            DependencyGraphService dependencyGraphService,
            ComplexityAnalyzer complexityAnalyzer,
            GraphMetricsAnalyzer graphMetricsAnalyzer,
            CentralityAnalyzer centralityAnalyzer,
            MetricNormalizationService normalizationService,
            MigrationImpactAnalyzer migrationImpactAnalyzer,
            MigrationSummaryService migrationSummaryService) {

        this.astService = astService;
        this.dependencyGraphService = dependencyGraphService;
        this.complexityAnalyzer = complexityAnalyzer;
        this.graphMetricsAnalyzer = graphMetricsAnalyzer;
        this.centralityAnalyzer = centralityAnalyzer;
        this.normalizationService = normalizationService;
        this.migrationImpactAnalyzer = migrationImpactAnalyzer;
        this.migrationSummaryService = migrationSummaryService;
    }

    public ProjectSummary processZip(MultipartFile file) throws IOException {

        /*
         * ============================================================
         * STEP 1: CREATE TEMPORARY DIRECTORY
         * ============================================================
         */

        Path tempDir =
                Files.createTempDirectory("uploadedProject");


        /*
         * ============================================================
         * STEP 2: EXTRACT ZIP
         * ============================================================
         */

        try (ZipInputStream zis =
                     new ZipInputStream(file.getInputStream())) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                Path filePath =
                        tempDir.resolve(entry.getName());

                if (entry.isDirectory()) {

                    Files.createDirectories(filePath);

                } else {

                    Files.createDirectories(
                            filePath.getParent()
                    );

                    Files.copy(
                            zis,
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

                zis.closeEntry();
            }
        }


        /*
         * ============================================================
         * STEP 3: COUNT JAVA FILES
         * ============================================================
         */

        int javaFiles =
                (int) Files.walk(tempDir)
                        .filter(path ->
                                path.toString().endsWith(".java"))
                        .count();


        /*
         * ============================================================
         * STEP 4: PROJECT NAME
         * ============================================================
         */

        String projectName =
                file.getOriginalFilename();

        if (projectName != null &&
                projectName.endsWith(".zip")) {

            projectName =
                    projectName.substring(
                            0,
                            projectName.length() - 4
                    );
        }


        /*
         * ============================================================
         * STEP 5: EXISTING AST ANALYSIS
         * ============================================================
         */

        List<Map<String, Object>> astResults =
                astService.analyzeRepository(tempDir);


        /*
         * ============================================================
         * STEP 6: BUILD DEPENDENCY GRAPH
         * ============================================================
         *
         * We build the JSON graph for the frontend AND
         * obtain the actual JGraphT graph for backend analysis.
         */

        Map<String, Object> graphJson =
                dependencyGraphService.buildGraph(tempDir);

        Graph<String, DefaultEdge> graph =
                dependencyGraphService.buildGraphObject(tempDir);


        /*
         * ============================================================
         * STEP 7: CREATE PROJECT SUMMARY
         * ============================================================
         */

        ProjectSummary summary =
                new ProjectSummary(
                        projectName,
                        javaFiles
                );

        summary.setAstAnalysis(astResults);

        summary.setDependencyGraph(graphJson);


        /*
         * ============================================================
         * STEP 8: COMPONENT COMPLEXITY ANALYSIS
         * ============================================================
         *
         * Parse each Java file and analyze every class/interface.
         */

        List<ComponentAnalysis> components =
                new ArrayList<>();

        try (var walk = Files.walk(tempDir)) {

            List<Path> javaPaths =
                    walk.filter(path ->
                                    path.toString().endsWith(".java"))
                            .toList();

            for (Path javaFile : javaPaths) {

                try {

                    CompilationUnit compilationUnit =
                            StaticJavaParser.parse(javaFile);

                    /*
                     * Find every class/interface in this file.
                     */
                    List<ClassOrInterfaceDeclaration> classes =
                            compilationUnit.findAll(
                                    ClassOrInterfaceDeclaration.class
                            );

                    for (ClassOrInterfaceDeclaration clazz :
                            classes) {

                        /*
                         * Path relative to uploaded repository.
                         */
                        String sourceFile =
                                tempDir
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

                } catch (Exception ignored) {

                    /*
                     * If one file cannot be parsed, skip it.
                     *
                     * AstService already handles/report parse
                     * failures, so one bad file should not stop
                     * analysis of the entire repository.
                     */
                }
            }
        }


        /*
         * ============================================================
         * STEP 9: GRAPH METRICS
         * ============================================================
         *
         * Calculates:
         *
         * - Fan-in
         * - Fan-out
         * - Blast radius
         */

        graphMetricsAnalyzer.analyze(
                graph,
                components
        );


        /*
         * ============================================================
         * STEP 10: CENTRALITY
         * ============================================================
         *
         * Calculates PageRank centrality for every component.
         */

        centralityAnalyzer.analyze(
                graph,
                components
        );


        /*
         * ============================================================
         * STEP 11: NORMALIZATION
         * ============================================================
         *
         * Calculates:
         *
         * - Complexity score
         * - Migration issue score
         *
         * Graph metrics are normalized later inside
         * MigrationImpactAnalyzer.
         */

        normalizationService.normalize(
                components
        );


        /*
         * ============================================================
         * STEP 12: MIGRATION IMPACT / RISK
         * ============================================================
         *
         * Calculates:
         *
         * - Difficulty
         * - Impact
         * - Risk
         */

        migrationImpactAnalyzer.calculate(
                components
        );


        /*
         * ============================================================
         * STEP 13: MIGRATION SUMMARY
         * ============================================================
         *
         * Calculates:
         *
         * - Total components
         * - High-risk components
         * - Medium-risk components
         * - Low-risk components
         * - Average risk
         * - Highest risk
         */

        MigrationSummary migrationSummary =
                migrationSummaryService.calculate(
                        components
                );


        /*
         * ============================================================
         * STEP 14: ATTACH ANALYSIS TO PROJECT SUMMARY
         * ============================================================
         */

        summary.setComponentAnalyses(
                components
        );

        summary.setMigrationSummary(
                migrationSummary
        );


        /*
         * ============================================================
         * FINAL RESULT
         * ============================================================
         */

        return summary;
    }
}
package com.codemorph.backend.service.roadmap;

import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.model.roadmap.MigrationRoadmap;
import com.codemorph.backend.model.roadmap.MigrationStep;
import com.codemorph.backend.model.roadmap.MigrationWave;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MigrationRoadmapServiceImpl
        implements MigrationRoadmapService {

    private final MigrationRoadmapScheduler scheduler;

    public MigrationRoadmapServiceImpl(
            MigrationRoadmapScheduler scheduler) {

        this.scheduler = scheduler;
    }

    @Override
    public MigrationRoadmap generateRoadmap(
            Graph<String, DefaultEdge> graph,
            List<ComponentAnalysis> components) {

        MigrationRoadmap roadmap =
                new MigrationRoadmap();

        if (graph == null ||
                components == null ||
                components.isEmpty()) {

            return roadmap;
        }

        MigrationDependencyResolver resolver =
                new JGraphTMigrationDependencyResolver(
                        graph
                );

        Map<String, MigrationStep> steps =
                new LinkedHashMap<>();

        /*
         * Convert EXISTING ComponentAnalysis objects.
         *
         * We do NOT recalculate impact/risk.
         */
        for (ComponentAnalysis analysis :
                components) {

            MigrationStep step =
                    new MigrationStep();

            step.setClassName(
                    analysis.getClassName()
            );

            step.setPackageName(
                    analysis.getPackageName()
            );

            step.setImpactScore(
                    analysis.getImpactScore()
            );

            step.setDifficultyScore(
                    analysis.getDifficultyScore()
            );

            step.setRiskScore(
                    analysis.getRiskScore()
            );

            step.setDependencies(
                    new ArrayList<>(
                            resolver.getDependencies(
                                    analysis.getClassName()
                            )
                    )
            );

            step.setDependents(
                    new ArrayList<>(
                            resolver.getDependents(
                                    analysis.getClassName()
                            )
                    )
            );

            steps.put(
                    analysis.getClassName(),
                    step
            );
        }

        /*
         * Detect dependency cycles.
         */
        DependencyCycleDetector cycleDetector =
                new DependencyCycleDetectorImpl(
                        graph
                );

        List<List<String>> cycles =
                cycleDetector.findCycles();

        roadmap.setDependencyCycles(
                cycles
        );

        /*
         * Generate migration waves.
         */
        List<MigrationWave> waves =
                scheduler.schedule(
                        resolver,
                        steps
                );

        roadmap.setWaves(waves);

        roadmap.setTotalWaves(
                waves.size()
        );

        /*
         * Find components that could not be scheduled.
         */
        List<String> scheduled =
                new ArrayList<>();

        for (MigrationWave wave :
                waves) {

            for (MigrationStep step :
                    wave.getComponents()) {

                scheduled.add(
                        step.getClassName()
                );
            }
        }

        List<String> blocked =
                new ArrayList<>();

        for (String component :
                graph.vertexSet()) {

            if (!scheduled.contains(component)) {

                blocked.add(component);

                MigrationStep step =
                        steps.get(component);

                if (step != null) {

                    step.setStatus(
                            "BLOCKED"
                    );

                    step.setReason(
                            "Cannot be scheduled because "
                                    + "dependency constraints "
                                    + "remain unresolved."
                    );
                }
            }
        }

        roadmap.setBlockedComponents(
                blocked
        );

        return roadmap;
    }
}
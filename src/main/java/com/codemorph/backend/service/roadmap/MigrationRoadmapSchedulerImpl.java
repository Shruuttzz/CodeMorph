package com.codemorph.backend.service.roadmap;

import com.codemorph.backend.model.roadmap.MigrationStep;
import com.codemorph.backend.model.roadmap.MigrationWave;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MigrationRoadmapSchedulerImpl
        implements MigrationRoadmapScheduler {

    @Override
    public List<MigrationWave> schedule(
            MigrationDependencyResolver resolver,
            Map<String, MigrationStep> components) {

        List<MigrationWave> waves =
                new ArrayList<>();

        Set<String> remaining =
                new LinkedHashSet<>(
                        components.keySet()
                );

        Set<String> migrated =
                new HashSet<>();

        int waveNumber = 1;

        while (!remaining.isEmpty()) {

            List<String> ready =
                    new ArrayList<>();

            /*
             * A component is ready only when every
             * dependency has already been migrated.
             */
            for (String component :
                    remaining) {

                Set<String> dependencies =
                        resolver.getDependencies(
                                component
                        );

                if (migrated.containsAll(
                        dependencies)) {

                    ready.add(component);
                }
            }

            /*
             * No ready components means that the
             * remaining graph contains unresolved
             * dependency cycles.
             */
            if (ready.isEmpty()) {
                break;
            }

            /*
             * Among the currently feasible components,
             * highest migration impact gets priority.
             */
            ready.sort(
                    Comparator
                            .comparingDouble(
                                    (String name) ->
                                            components
                                                    .get(name)
                                                    .getImpactScore()
                            )
                            .reversed()
                            .thenComparing(
                                    name ->
                                            components
                                                    .get(name)
                                                    .getClassName()
                            )
            );

            MigrationWave wave =
                    new MigrationWave(
                            waveNumber
                    );

            for (String component :
                    ready) {

                MigrationStep step =
                        components.get(component);

                step.setWaveNumber(
                        waveNumber
                );

                step.setStatus(
                        "SCHEDULED"
                );

                if (step.getDependencies()
                        .isEmpty()) {

                    step.setReason(
                            "No unresolved dependencies."
                    );

                } else {

                    step.setReason(
                            "All dependencies have been "
                                    + "migrated. Prioritized "
                                    + "by migration impact."
                    );
                }

                wave.getComponents().add(step);
            }

            calculateWaveStatistics(wave);

            wave.setObjective(
                    "Migrate dependency-feasible components "
                            + "prioritized by migration impact."
            );

            waves.add(wave);

            /*
             * All components in this wave are considered
             * migrated before evaluating the next wave.
             */
            migrated.addAll(ready);
            remaining.removeAll(ready);

            waveNumber++;
        }

        return waves;
    }

    private void calculateWaveStatistics(
            MigrationWave wave) {

        if (wave.getComponents().isEmpty()) {
            wave.setAverageImpact(0);
            wave.setAverageRisk(0);
            return;
        }

        double totalImpact = 0;
        double totalRisk = 0;

        for (MigrationStep step :
                wave.getComponents()) {

            totalImpact +=
                    step.getImpactScore();

            totalRisk +=
                    step.getRiskScore();
        }

        int count =
                wave.getComponents().size();

        wave.setAverageImpact(
                round(totalImpact / count)
        );

        wave.setAverageRisk(
                round(totalRisk / count)
        );
    }

    private double round(double value) {

        return Math.round(value * 100.0)
                / 100.0;
    }
}
package com.codemorph.backend.service;


import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.Util.NormalizationUtil;

import java.util.List;
import java.util.function.ToDoubleFunction;

@Service
public class MigrationImpactAnalyzer {

    /*
     * Difficulty weights
     */
    private static final double API_WEIGHT = 0.40;
    private static final double COMPLEXITY_WEIGHT = 0.30;
    private static final double COMPILER_WEIGHT = 0.30;

    /*
     * Impact weights
     */
    private static final double FAN_IN_WEIGHT = 0.30;
    private static final double BLAST_RADIUS_WEIGHT = 0.50;
    private static final double CENTRALITY_WEIGHT = 0.20;

    /*
     * Overall risk weights
     */
    private static final double DIFFICULTY_WEIGHT = 0.60;
    private static final double IMPACT_WEIGHT = 0.40;

    public void calculate(
            List<ComponentAnalysis> components) {

        if (components == null ||
                components.isEmpty()) {
            return;
        }

        /*
         * First normalize graph metrics.
         */

        double minFanIn =
                min(
                        components,
                        ComponentAnalysis::getFanIn
                );

        double maxFanIn =
                max(
                        components,
                        ComponentAnalysis::getFanIn
                );

        double minBlast =
                min(
                        components,
                        ComponentAnalysis::getBlastRadius
                );

        double maxBlast =
                max(
                        components,
                        ComponentAnalysis::getBlastRadius
                );

        double minCentrality =
                min(
                        components,
                        ComponentAnalysis::getCentrality
                );

        double maxCentrality =
                max(
                        components,
                        ComponentAnalysis::getCentrality
                );

        for (ComponentAnalysis component :
                components) {

            /*
             * ---------------------------------------
             * 1. API / migration issue score
             * ---------------------------------------
             *
             * Already normalized 0–100.
             */
            double apiScore =
                    NormalizationUtil.clamp(
                            component.getMigrationIssueScore()
                    );

            /*
             * ---------------------------------------
             * 2. Complexity score
             * ---------------------------------------
             */
            double complexityScore =
                    NormalizationUtil.clamp(
                            component.getComplexityScore()
                    );

            /*
             * ---------------------------------------
             * 3. Compiler evidence
             * ---------------------------------------
             *
             * NOT AVAILABLE YET.
             *
             * We must NOT pretend compiler evidence
             * is zero.
             *
             * Re-normalize available weights.
             *
             * Available:
             * API = 0.4
             * Complexity = 0.3
             *
             * Total = 0.7
             */
            double difficulty =
                    (
                            API_WEIGHT * apiScore
                                    + COMPLEXITY_WEIGHT * complexityScore
                    )
                            / (API_WEIGHT + COMPLEXITY_WEIGHT);

            /*
             * ---------------------------------------
             * 4. Normalize graph metrics
             * ---------------------------------------
             */

            double fanInScore =
                    NormalizationUtil.minMax(
                            component.getFanIn(),
                            minFanIn,
                            maxFanIn
                    );

            double blastRadiusScore =
                    NormalizationUtil.minMax(
                            component.getBlastRadius(),
                            minBlast,
                            maxBlast
                    );

            double centralityScore =
                    NormalizationUtil.minMax(
                            component.getCentrality(),
                            minCentrality,
                            maxCentrality
                    );

            /*
             * ---------------------------------------
             * 5. IMPACT
             *
             * I(v) =
             * 0.3F + 0.5B + 0.2S
             * ---------------------------------------
             */

            double impact =
                    FAN_IN_WEIGHT * fanInScore
                            + BLAST_RADIUS_WEIGHT * blastRadiusScore
                            + CENTRALITY_WEIGHT * centralityScore;

            /*
             * ---------------------------------------
             * 6. RISK
             *
             * R(v) =
             * 100 * [0.6D + 0.4I]
             *
             * Since D and I are already 0–100,
             * do NOT multiply the whole expression
             * by another 100.
             * ---------------------------------------
             */

            double risk =
                    DIFFICULTY_WEIGHT * difficulty
                            + IMPACT_WEIGHT * impact;

            component.setDifficultyScore(
                    round(difficulty)
            );

            component.setImpactScore(
                    round(impact)
            );

            component.setRiskScore(
                    round(
                            NormalizationUtil.clamp(risk)
                    )
            );
        }
    }

    private double min(
            List<ComponentAnalysis> components,
            ToDoubleFunction<ComponentAnalysis> function) {

        return components.stream()
                .mapToDouble(function)
                .min()
                .orElse(0.0);
    }

    private double max(
            List<ComponentAnalysis> components,
            ToDoubleFunction<ComponentAnalysis> function) {

        return components.stream()
                .mapToDouble(function)
                .max()
                .orElse(0.0);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

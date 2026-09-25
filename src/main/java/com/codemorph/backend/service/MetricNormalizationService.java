package com.codemorph.backend.service;

import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.Util.NormalizationUtil;

import java.util.List;
import java.util.function.ToDoubleFunction;

@Service
public class MetricNormalizationService {

    public void normalize(List<ComponentAnalysis> components) {

        if (components == null || components.isEmpty()) {
            return;
        }

        double minLoc = min(components, ComponentAnalysis::getLoc);
        double maxLoc = max(components, ComponentAnalysis::getLoc);

        double minMethods = min(
                components,
                ComponentAnalysis::getMethodCount
        );

        double maxMethods = max(
                components,
                ComponentAnalysis::getMethodCount
        );

        double minFields = min(
                components,
                ComponentAnalysis::getFieldCount
        );

        double maxFields = max(
                components,
                ComponentAnalysis::getFieldCount
        );

        double minCyclomatic = min(
                components,
                ComponentAnalysis::getCyclomaticComplexity
        );

        double maxCyclomatic = max(
                components,
                ComponentAnalysis::getCyclomaticComplexity
        );

        double minInheritance = min(
                components,
                ComponentAnalysis::getInheritanceDepth
        );

        double maxInheritance = max(
                components,
                ComponentAnalysis::getInheritanceDepth
        );

        double minDeprecated = min(
                components,
                ComponentAnalysis::getDeprecatedApiCount
        );

        double maxDeprecated = max(
                components,
                ComponentAnalysis::getDeprecatedApiCount
        );

        double minFanIn = min(
                components,
                ComponentAnalysis::getFanIn
        );

        double maxFanIn = max(
                components,
                ComponentAnalysis::getFanIn
        );

        double minBlast = min(
                components,
                ComponentAnalysis::getBlastRadius
        );

        double maxBlast = max(
                components,
                ComponentAnalysis::getBlastRadius
        );

        double minCentrality = min(
                components,
                ComponentAnalysis::getCentrality
        );

        double maxCentrality = max(
                components,
                ComponentAnalysis::getCentrality
        );

        for (ComponentAnalysis c : components) {

            double locScore =
                    NormalizationUtil.minMax(
                            c.getLoc(),
                            minLoc,
                            maxLoc
                    );

            double methodScore =
                    NormalizationUtil.minMax(
                            c.getMethodCount(),
                            minMethods,
                            maxMethods
                    );

            double fieldScore =
                    NormalizationUtil.minMax(
                            c.getFieldCount(),
                            minFields,
                            maxFields
                    );

            double cyclomaticScore =
                    NormalizationUtil.minMax(
                            c.getCyclomaticComplexity(),
                            minCyclomatic,
                            maxCyclomatic
                    );

            double inheritanceScore =
                    NormalizationUtil.minMax(
                            c.getInheritanceDepth(),
                            minInheritance,
                            maxInheritance
                    );

            /*
             * Initial complexity model:
             *
             * 30% LOC
             * 20% methods
             * 10% fields
             * 30% cyclomatic complexity
             * 10% inheritance depth
             */
            double complexityScore =
                    0.30 * locScore
                            + 0.20 * methodScore
                            + 0.10 * fieldScore
                            + 0.30 * cyclomaticScore
                            + 0.10 * inheritanceScore;

            c.setComplexityScore(
                    NormalizationUtil.clamp(complexityScore)
            );

            double deprecatedScore =
                    NormalizationUtil.minMax(
                            c.getDeprecatedApiCount(),
                            minDeprecated,
                            maxDeprecated
                    );

            c.setMigrationIssueScore(deprecatedScore);

            /*
             * Store normalized graph metrics temporarily
             * by overwriting only when required by the final
             * calculation would be dangerous.
             *
             * Therefore the final analyzer will normalize these
             * separately.
             */
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
}

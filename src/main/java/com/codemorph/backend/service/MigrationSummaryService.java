package com.codemorph.backend.service;

import org.springframework.stereotype.Service;
import com.codemorph.backend.model.ComponentAnalysis;
import com.codemorph.backend.model.MigrationSummary;

import java.util.List;

@Service
public class MigrationSummaryService {

    public MigrationSummary calculate(
            List<ComponentAnalysis> components) {

        MigrationSummary summary =
                new MigrationSummary();

        if (components == null ||
                components.isEmpty()) {

            return summary;
        }

        summary.setTotalComponents(
                components.size()
        );

        int high = 0;
        int medium = 0;
        int low = 0;

        double totalRisk = 0;
        double highestRisk = 0;

        for (ComponentAnalysis component :
                components) {

            double risk =
                    component.getRiskScore();

            totalRisk += risk;

            highestRisk =
                    Math.max(
                            highestRisk,
                            risk
                    );

            if (risk >= 67) {
                high++;
            }
            else if (risk >= 34) {
                medium++;
            }
            else {
                low++;
            }
        }

        summary.setHighRiskComponents(high);
        summary.setMediumRiskComponents(medium);
        summary.setLowRiskComponents(low);

        summary.setAverageRisk(
                round(
                        totalRisk / components.size()
                )
        );

        summary.setHighestRisk(
                round(highestRisk)
        );

        return summary;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
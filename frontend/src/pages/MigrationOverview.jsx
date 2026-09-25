import React from "react";

export default function MigrationOverview({ summary }) {

    if (!summary) {
        return <p>No migration analysis available.</p>;
    }

    return (
        <div>
            <h2>Migration Risk Overview</h2>

            <div>
                <h3>Total Components</h3>
                <p>{summary.totalComponents}</p>
            </div>

            <div>
                <h3>High Risk</h3>
                <p>{summary.highRiskComponents}</p>
            </div>

            <div>
                <h3>Medium Risk</h3>
                <p>{summary.mediumRiskComponents}</p>
            </div>

            <div>
                <h3>Low Risk</h3>
                <p>{summary.lowRiskComponents}</p>
            </div>

            <div>
                <h3>Average Risk</h3>
                <p>{summary.averageRisk}</p>
            </div>

            <div>
                <h3>Highest Risk</h3>
                <p>{summary.highestRisk}</p>
            </div>
        </div>
    );
}
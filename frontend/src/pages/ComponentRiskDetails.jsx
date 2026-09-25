import React from "react";

export default function ComponentRiskDetails({
                                                 component
                                             }) {

    if (!component) {
        return (
            <p>
                Select a component to view its analysis.
            </p>
        );
    }

    return (
        <div>

            <h2>
                {component.className}
            </h2>

            <h3>
                Risk: {component.riskScore}/100
            </h3>

            <hr />

            <h3>Scores</h3>

            <p>
                Difficulty:
                {" "}
                {component.difficultyScore}
            </p>

            <p>
                Impact:
                {" "}
                {component.impactScore}
            </p>

            <p>
                Risk:
                {" "}
                {component.riskScore}
            </p>

            <h3>Complexity</h3>

            <p>
                LOC:
                {" "}
                {component.loc}
            </p>

            <p>
                Methods:
                {" "}
                {component.methodCount}
            </p>

            <p>
                Fields:
                {" "}
                {component.fieldCount}
            </p>

            <p>
                Branches:
                {" "}
                {component.branchCount}
            </p>

            <p>
                Cyclomatic Complexity:
                {" "}
                {component.cyclomaticComplexity}
            </p>

            <p>
                Inheritance Depth:
                {" "}
                {component.inheritanceDepth}
            </p>

            <h3>Migration Evidence</h3>

            <p>
                Deprecated APIs:
                {" "}
                {component.deprecatedApiCount}
            </p>

            <p>
                Migration Issue Score:
                {" "}
                {component.migrationIssueScore}
            </p>

            <h3>Graph Metrics</h3>

            <p>
                Fan-in:
                {" "}
                {component.fanIn}
            </p>

            <p>
                Fan-out:
                {" "}
                {component.fanOut}
            </p>

            <p>
                Blast Radius:
                {" "}
                {component.blastRadius}
            </p>

            <p>
                Centrality:
                {" "}
                {component.centrality}
            </p>

        </div>
    );
}
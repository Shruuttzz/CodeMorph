import React from "react";
import { useNavigate } from "react-router-dom";
import { useProject } from "../context/ProjectContext";


export default function MigrationOverview() {
    const navigate = useNavigate();

    // Get the analysed project from shared ProjectContext
    const { projectResult } = useProject();

    const result = projectResult;

    const summary =
        result?.migrationSummary;



    // =========================
    // NO PROJECT AVAILABLE
    // =========================

    if (!result || !summary) {
        return (
            <div className="upload-page">

                <div className="upload-card">

                    <h1>
                        No Migration Analysis Available
                    </h1>

                    <p className="subtitle">
                        Please upload and analyze a Java project first.
                    </p>

                    <button
                        className="upload-btn"
                        onClick={() => navigate("/upload")}
                    >
                        Go to Upload
                    </button>

                </div>

            </div>
        );
    }


    return (
        <div className="upload-page">

            <div className="results-section">

                {/* =========================
                    BACK TO ANALYSIS
                ========================== */}

                <button
                    className="upload-btn"
                    onClick={() => navigate("/analysis")}
                    style={{
                        marginBottom: "30px"
                    }}
                >
                    ← Back to Analysis
                </button>


                {/* =========================
                    PAGE HEADING
                ========================== */}

                <h1 className="results-title">
                    Migration Risk Overview
                </h1>


                {/* =========================
                    SUMMARY CARDS
                ========================== */}

                <div className="summary-cards">

                    <div className="stat-card">

                        <span className="stat-number">
                            {summary.totalComponents}
                        </span>

                        <span className="stat-label">
                            Total Components
                        </span>

                    </div>


                    <div className="stat-card stat-danger">

                        <span className="stat-number">
                            {summary.highRiskComponents}
                        </span>

                        <span className="stat-label">
                            High Risk
                        </span>

                    </div>


                    <div className="stat-card stat-warning">

                        <span className="stat-number">
                            {summary.mediumRiskComponents}
                        </span>

                        <span className="stat-label">
                            Medium Risk
                        </span>

                    </div>


                    <div className="stat-card">

                        <span className="stat-number">
                            {summary.lowRiskComponents}
                        </span>

                        <span className="stat-label">
                            Low Risk
                        </span>

                    </div>


                    <div className="stat-card">

                        <span className="stat-number">
                            {summary.averageRisk}
                        </span>

                        <span className="stat-label">
                            Average Risk
                        </span>

                    </div>


                    <div className="stat-card stat-danger">

                        <span className="stat-number">
                            {summary.highestRisk}
                        </span>

                        <span className="stat-label">
                            Highest Risk
                        </span>

                    </div>

                </div>


                {/* =========================
                    COMPONENT RISK TABLE
                ========================== */}

                <h2
                    className="results-title"
                    style={{
                        marginTop: "50px"
                    }}
                >
                    Component-wise Migration Risk
                </h2>


                <div
                    style={{
                        overflowX: "auto"
                    }}
                >

                    <table className="ast-table">

                        <thead>

                        <tr>

                            <th>Component</th>

                            <th>LOC</th>

                            <th>Complexity</th>

                            <th>Fan-in</th>

                            <th>Fan-out</th>

                            <th>Blast Radius</th>

                            <th>Centrality</th>

                            <th>Difficulty</th>

                            <th>Impact</th>

                            <th>Risk</th>

                        </tr>

                        </thead>




                        <tbody>

                        {result.componentAnalyses?.map(
                            (component, index) => (

                                <tr key={index}>

                                    <td>
                                        <strong>
                                            {component.className}
                                        </strong>
                                    </td>


                                    <td>
                                        {component.loc}
                                    </td>


                                    <td>
                                        {component.complexityScore !==
                                        undefined
                                            ? component.complexityScore.toFixed(
                                                2
                                            )
                                            : "—"}
                                    </td>


                                    <td>
                                        {component.fanIn ?? "—"}
                                    </td>


                                    <td>
                                        {component.fanOut ?? "—"}
                                    </td>


                                    <td>
                                        {component.blastRadius ?? "—"}
                                    </td>


                                    <td>
                                        {component.centrality !==
                                        undefined
                                            ? component.centrality.toFixed(
                                                4
                                            )
                                            : "—"}
                                    </td>


                                    <td>
                                        {component.difficultyScore !==
                                        undefined
                                            ? component.difficultyScore.toFixed(
                                                2
                                            )
                                            : "—"}
                                    </td>


                                    <td>
                                        {component.impactScore !==
                                        undefined
                                            ? component.impactScore.toFixed(
                                                2
                                            )
                                            : "—"}
                                    </td>


                                    <td>

                                        <span
                                            className={
                                                `badge ${
                                                    component.riskScore >= 67
                                                        ? "badge-error"
                                                        : component.riskScore >= 34
                                                            ? "badge-warning"
                                                            : "badge-success"
                                                }`
                                            }
                                        >

                                            {component.riskScore !==
                                            undefined
                                                ? component.riskScore.toFixed(
                                                    2
                                                )
                                                : "—"}



                                        </span>

                                    </td>

                                </tr>

                            )
                        )}

                        </tbody>

                    </table>

                </div>

                <div className="roadmap-navigation">

                    <div>
                        <h2>
                            Ready to plan the migration?
                        </h2>

                        <p>
                            See the recommended migration
                            order in a simple visual roadmap.
                        </p>
                    </div>

                    <button
                        className="view-roadmap-button"
                        onClick={() =>
                            navigate(
                                "/migration-roadmap"
                            )
                        }
                    >
                        View Migration Roadmap →
                    </button>

                </div>

            </div>

        </div>
    );
}
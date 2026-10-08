import React from "react";
import { useNavigate } from "react-router-dom";
import { useProject } from "../context/ProjectContext";

export default function MigrationRoadmapPage() {
    const navigate = useNavigate();
    const { projectResult } = useProject();

    const roadmap = projectResult?.migrationRoadmap;

    const waves = Array.isArray(roadmap?.waves)
        ? roadmap.waves
        : [];

    const projectName =
        projectResult?.projectName ||
        projectResult?.projectSummary?.projectName ||
        "Migration Project";


    if (!roadmap) {
        return (
            <div className="roadmap-page">
                <div className="roadmap-empty-page">

                    <h2>
                        Migration Roadmap
                    </h2>

                    <p>
                        No migration roadmap is available.
                    </p>

                    <button
                        className="roadmap-back-button"
                        onClick={() =>
                            navigate("/migration-analysis")
                        }
                    >
                        ← Back to Risk Overview
                    </button>

                </div>
            </div>
        );
    }


    return (
        <div className="roadmap-page">

            {/* =================================================
                PAGE HEADER
            ================================================== */}

            <div className="roadmap-page-header">

                <p className="roadmap-eyebrow">
                    Migration Plan
                </p>

                <h1>
                    Your Migration Roadmap
                </h1>

                <p className="roadmap-page-description">
                    Follow the recommended migration order
                    from the project foundation through each
                    migration wave and its components.
                </p>

            </div>


            {/* =================================================
                BACK TO RISK OVERVIEW
            ================================================== */}

            <button
                className="roadmap-back-button"
                onClick={() =>
                    navigate("/migration-analysis")
                }
            >
                ← Back to Risk Overview
            </button>


            {/* =================================================
                DIAGRAM CONTAINER
            ================================================== */}

            <div className="roadmap-diagram-container">

                {/* =================================================
                    TECHNICAL DOCUMENTATION
                    TOP-LEFT OF DIAGRAM
                ================================================== */}

                <div className="technical-button-wrapper">

                    <button
                        type="button"
                        className="technical-doc-button"
                        onClick={() =>
                            navigate(
                                "/migration-roadmap/documentation"
                            )
                        }
                    >
                        View Technical Documentation
                    </button>

                </div>


                {/* =================================================
                    ACTUAL FLOW DIAGRAM
                ================================================== */}

                {waves.length === 0 ? (

                    <div className="roadmap-empty">
                        No migration waves are available.
                    </div>

                ) : (

                    <div className="roadmap-diagram-scroll">

                        <div className="roadmap-diagram-inner">

                            {/* =====================================
                                PROJECT ROOT
                            ====================================== */}

                            <div className="roadmap-project-root">

                                <span className="root-label">
                                    PROJECT
                                </span>

                                <strong>
                                    {projectName}
                                </strong>

                            </div>


                            {/* =====================================
                                PROJECT → WAVES CONNECTOR
                            ====================================== */}

                            <div
                                className="project-to-waves-line"
                                aria-hidden="true"
                            />


                            {/* =====================================
                                WAVES
                            ====================================== */}

                            <div
                                className="roadmap-waves"
                                style={{
                                    "--wave-count":
                                    waves.length
                                }}
                            >

                                {waves.map(
                                    (wave, waveIndex) => {

                                        const components =
                                            Array.isArray(
                                                wave.components
                                            )
                                                ? wave.components
                                                : [];

                                        const waveNumber =
                                            wave.waveNumber ??
                                            waveIndex + 1;


                                        return (

                                            <div
                                                className="roadmap-wave-column"
                                                key={
                                                    `wave-${waveNumber}-${waveIndex}`
                                                }
                                            >

                                                {/* =================================
                                                    WAVE BRANCH
                                                ================================== */}

                                                <div
                                                    className="wave-top-branch"
                                                    aria-hidden="true"
                                                />


                                                {/* =================================
                                                    WAVE BOX
                                                ================================== */}

                                                <div className="roadmap-wave-box">

                                                    <span className="wave-label">
                                                        WAVE {waveNumber}
                                                    </span>

                                                    <strong className="wave-count">

                                                        {components.length}{" "}

                                                        {components.length === 1
                                                            ? "Component"
                                                            : "Components"}

                                                    </strong>

                                                    <p>

                                                        {wave.objective ||
                                                            "Components that can be migrated at this stage."}

                                                    </p>

                                                </div>


                                                {/* =================================
                                                    COMPONENTS
                                                ================================== */}

                                                <div className="wave-components">

                                                    {components.length === 0 ? (

                                                        <div className="no-components">
                                                            No components
                                                            in this wave.
                                                        </div>

                                                    ) : (

                                                        components.map(
                                                            (
                                                                component,
                                                                componentIndex
                                                            ) => {

                                                                const riskScore =
                                                                    typeof component?.riskScore ===
                                                                    "number"
                                                                        ? component.riskScore
                                                                        : null;

                                                                const riskLevel =
                                                                    getRiskLevel(
                                                                        riskScore
                                                                    );


                                                                return (

                                                                    <div
                                                                        className="roadmap-component-row"
                                                                        key={
                                                                            `${component?.className || "component"}-${componentIndex}`
                                                                        }
                                                                    >

                                                                        {/* =================================
                                                                            COMPONENT BRANCH
                                                                        ================================== */}

                                                                        <div
                                                                            className="component-horizontal-line"
                                                                            aria-hidden="true"
                                                                        />


                                                                        {/* =================================
                                                                            COMPONENT / FILE BOX
                                                                        ================================== */}

                                                                        <div className="roadmap-component-box">

                                                                            <span className="component-box-label">
                                                                                COMPONENT
                                                                            </span>

                                                                            <strong>
                                                                                {
                                                                                    component?.className ||
                                                                                    "Unnamed Component"
                                                                                }
                                                                            </strong>

                                                                            <p>
                                                                                {getSimpleExplanation(
                                                                                    component
                                                                                )}
                                                                            </p>

                                                                        </div>


                                                                        {/* =================================
                                                                            RISK BOX
                                                                        ================================== */}

                                                                        <div
                                                                            className={
                                                                                `roadmap-risk-box risk-${riskLevel.toLowerCase()}`
                                                                            }
                                                                        >

                                                                            <span className="risk-box-label">
                                                                                RISK
                                                                            </span>

                                                                            <strong>
                                                                                {
                                                                                    riskScore !==
                                                                                    null
                                                                                        ? riskScore.toFixed(
                                                                                            2
                                                                                        )
                                                                                        : "—"
                                                                                }
                                                                            </strong>

                                                                            <small>
                                                                                {
                                                                                    riskLevel
                                                                                }
                                                                            </small>

                                                                        </div>

                                                                    </div>

                                                                );
                                                            }
                                                        )

                                                    )}

                                                </div>

                                            </div>

                                        );
                                    }
                                )}

                            </div>

                        </div>

                    </div>

                )}

            </div>


            {/* =================================================
                HOW TO READ
            ================================================== */}

            <div className="roadmap-help">

                <h2>
                    How to read this roadmap
                </h2>

                <div className="help-grid">

                    <div className="help-item">

                        <span className="help-number">
                            1
                        </span>

                        <div>

                            <strong>
                                Start with the first wave
                            </strong>

                            <p>
                                Wave 1 contains the components
                                that can be migrated first.
                            </p>

                        </div>

                    </div>


                    <div className="help-item">

                        <span className="help-number">
                            2
                        </span>

                        <div>

                            <strong>
                                Follow the waves in order
                            </strong>

                            <p>
                                Later waves contain components
                                that depend on earlier migration
                                work.
                            </p>

                        </div>

                    </div>


                    <div className="help-item">

                        <span className="help-number">
                            3
                        </span>

                        <div>

                            <strong>
                                Check the risk beside each component
                            </strong>

                            <p>
                                The risk box shows the migration
                                risk score for that component.
                            </p>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}


/* =========================================================
   RISK LEVEL
========================================================= */

function getRiskLevel(score) {

    if (typeof score !== "number") {
        return "UNKNOWN";
    }

    if (score >= 67) {
        return "HIGH";
    }

    if (score >= 34) {
        return "MEDIUM";
    }

    return "LOW";
}


/* =========================================================
   SIMPLE NON-TECHNICAL EXPLANATION
========================================================= */

function getSimpleExplanation(component) {

    const dependencies =
        Array.isArray(component?.dependencies)
            ? component.dependencies
            : [];


    if (dependencies.length === 0) {

        return (
            "This component can be started first."
        );
    }


    if (dependencies.length === 1) {

        return (
            `This should come after ${dependencies[0]} is ready.`
        );
    }


    return (
        `This should come after ${dependencies.length} other components are ready.`
    );
}
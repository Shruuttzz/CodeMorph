import React from "react";
import { useNavigate } from "react-router-dom";
import { useProject } from "../context/ProjectContext";

import "./MigrationRoadmapPage.css";

export default function MigrationRoadmapPage() {
    const navigate = useNavigate();
    const { projectResult } = useProject();

    const roadmap = projectResult?.migrationRoadmap;

    const waves = Array.isArray(roadmap?.waves)
        ? roadmap.waves
        : [];

    const projectName =
        projectResult?.projectName ||
        "Migration Project";


    if (!roadmap) {
        return (
            <div className="mr-page">

                <div className="mr-empty-page">

                    <h2>
                        Migration Roadmap
                    </h2>

                    <p>
                        No migration roadmap is available.
                    </p>

                    <button
                        type="button"
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
        <div className="mr-page">

            {/* =========================================
                HEADER
            ========================================== */}

            <div className="mr-header">

                <div>

                    <div className="mr-eyebrow">
                        MIGRATION PLAN
                    </div>

                    <h1>
                        Your Migration Roadmap
                    </h1>

                    <p>
                        Follow the recommended migration order
                        from the project foundation through each
                        migration wave and its components.
                    </p>

                </div>

            </div>


            {/* =========================================
                BACK BUTTON
            ========================================== */}

            <button
                type="button"
                className="mr-back-button"
                onClick={() =>
                    navigate("/migration-analysis")
                }
            >
                ← Back to Risk Overview
            </button>


            {/* =========================================
                DIAGRAM
            ========================================== */}

            <div className="mr-diagram-card">


                {/* =====================================
                    TECHNICAL DOCUMENTATION
                ====================================== */}

                <div className="mr-toolbar">

                    <button
                        type="button"
                        className="mr-documentation-button"
                        onClick={() =>
                            navigate(
                                "/migration-roadmap/documentation"
                            )
                        }
                    >
                        View Technical Documentation
                    </button>

                </div>


                {/* =====================================
                    SCROLLABLE DIAGRAM
                ====================================== */}

                <div className="mr-scroll">

                    <div className="mr-diagram">


                        {/* =================================
                            PROJECT ROOT
                        ================================== */}

                        <div className="mr-project">

                            <span>
                                PROJECT
                            </span>

                            <strong>
                                {projectName}
                            </strong>

                        </div>


                        {/* =================================
                            ROOT → WAVES
                        ================================== */}

                        <div className="mr-root-line" />


                        {/* =================================
                            WAVE COLUMNS
                        ================================== */}

                        <div
                            className="mr-waves"
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
                                            className="mr-wave-column"
                                            key={
                                                `wave-${waveNumber}`
                                            }
                                        >

                                            {/* =================
                                                WAVE BRANCH
                                            ================== */}

                                            <div className="mr-wave-branch" />


                                            {/* =================
                                                WAVE BOX
                                            ================== */}

                                            <div className="mr-wave">

                                                <span className="mr-wave-label">
                                                    WAVE {waveNumber}
                                                </span>

                                                <strong>
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


                                            {/* =================
                                                COMPONENT STACK
                                            ================== */}

                                            <div className="mr-components">

                                                {components.map(
                                                    (
                                                        component,
                                                        componentIndex
                                                    ) => {

                                                        const score =
                                                            typeof component?.riskScore ===
                                                            "number"
                                                                ? component.riskScore
                                                                : null;

                                                        const level =
                                                            getRiskLevel(
                                                                score
                                                            );


                                                        return (

                                                            <div
                                                                className="mr-component-row"
                                                                key={
                                                                    `${component?.className}-${componentIndex}`
                                                                }
                                                            >

                                                                {/* COMPONENT BRANCH */}

                                                                <div className="mr-component-branch" />


                                                                {/* ==================
                                                                    FILE / COMPONENT
                                                                =================== */}

                                                                <div className="mr-component">

                                                                    <span>
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


                                                                {/* ==================
                                                                    RISK
                                                                =================== */}

                                                                <div
                                                                    className={
                                                                        `mr-risk mr-risk-${level.toLowerCase()}`
                                                                    }
                                                                >

                                                                    <span>
                                                                        RISK
                                                                    </span>

                                                                    <strong>
                                                                        {
                                                                            score !== null
                                                                                ? score.toFixed(
                                                                                    2
                                                                                )
                                                                                : "—"
                                                                        }
                                                                    </strong>

                                                                    <small>
                                                                        {level}
                                                                    </small>

                                                                </div>

                                                            </div>

                                                        );
                                                    }
                                                )}

                                            </div>

                                        </div>

                                    );
                                }
                            )}

                        </div>

                    </div>

                </div>

            </div>


            {/* =========================================
                HOW TO READ
            ========================================== */}

            <div className="mr-help">

                <h2>
                    How to read this roadmap
                </h2>

                <div className="mr-help-grid">

                    <div>

                        <span>
                            1
                        </span>

                        <div>

                            <strong>
                                Start with Wave 1
                            </strong>

                            <p>
                                These are the components
                                that can be migrated first.
                            </p>

                        </div>

                    </div>


                    <div>

                        <span>
                            2
                        </span>

                        <div>

                            <strong>
                                Follow the waves
                            </strong>

                            <p>
                                Later waves contain components
                                whose dependencies are ready.
                            </p>

                        </div>

                    </div>


                    <div>

                        <span>
                            3
                        </span>

                        <div>

                            <strong>
                                Check the risk
                            </strong>

                            <p>
                                Every component has its
                                migration risk shown beside it.
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
   SIMPLE EXPLANATION
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
import React from "react";
import { useNavigate } from "react-router-dom";
import { useProject } from "../context/ProjectContext";

function formatScore(score) {
    return typeof score === "number"
        ? score.toFixed(2)
        : "—";
}

export default function MigrationRoadmapDocumentation() {

    const navigate = useNavigate();

    const { projectResult } = useProject();

    const roadmap =
        projectResult?.migrationRoadmap;

    const waves =
        Array.isArray(roadmap?.waves)
            ? roadmap.waves
            : [];


    if (!projectResult || !roadmap) {

        return (

            <div className="upload-page">

                <div className="upload-card">

                    <h1>
                        No Migration Roadmap Available
                    </h1>

                    <p className="subtitle">
                        Please upload and analyze a Java
                        project first.
                    </p>

                    <button
                        className="upload-btn"
                        onClick={() =>
                            navigate("/upload")
                        }
                    >
                        Go to Upload
                    </button>

                </div>

            </div>
        );
    }


    return (

        <div className="upload-page roadmap-doc-page">

            <div className="results-section">

                <button
                    className="upload-btn roadmap-back-button"
                    onClick={() =>
                        navigate("/migration-roadmap")
                    }
                >
                    ← Back to Migration Roadmap
                </button>


                <h1 className="results-title">

                    Migration Roadmap —
                    Technical Documentation

                </h1>


                <p className="roadmap-intro">

                    This page explains why components are
                    placed in different migration waves and
                    how the roadmap should be interpreted.

                </p>


                {/* =========================
                    GENERAL EXPLANATION
                ========================== */}

                <div className="documentation-card">

                    <h2>
                        How the migration waves work
                    </h2>

                    <p>
                        A migration wave is a group of
                        components that can be migrated at
                        the same stage of the plan.
                    </p>

                    <p>
                        Components with unresolved migration
                        dependencies must wait for the
                        components they depend on to be
                        migrated first.
                    </p>

                    <p>
                        Within the set of components that are
                        ready to migrate, migration impact
                        helps indicate which components should
                        receive greater priority.
                    </p>

                    <p>
                        Risk is displayed as supporting
                        information about each component
                        rather than replacing the dependency
                        constraints.
                    </p>

                </div>


                {/* =========================
                    WAVE ANALYSIS
                ========================== */}

                <div className="wave-documentation-list">

                    {waves.map(
                        (
                            wave,
                            index
                        ) => {

                            const components =
                                Array.isArray(
                                    wave.components
                                )
                                    ? wave.components
                                    : [];

                            const waveNumber =
                                wave.waveNumber ??
                                index + 1;


                            return (

                                <section
                                    className="documentation-wave-card"
                                    key={
                                        `documentation-wave-${waveNumber}-${index}`
                                    }
                                >

                                    <div className="documentation-wave-heading">

                                        <div>

                                            <span className="wave-label">
                                                WAVE {waveNumber}
                                            </span>

                                            <h2>
                                                {
                                                    wave.objective ||
                                                    `Migration stage ${waveNumber}`
                                                }
                                            </h2>

                                        </div>


                                        <div className="documentation-wave-stats">

                                            <span>
                                                Components:{" "}
                                                <strong>
                                                    {
                                                        components.length
                                                    }
                                                </strong>
                                            </span>

                                            <span>
                                                Avg. Impact:{" "}
                                                <strong>
                                                    {formatScore(
                                                        wave.averageImpact
                                                    )}
                                                </strong>
                                            </span>

                                            <span>
                                                Avg. Risk:{" "}
                                                <strong>
                                                    {formatScore(
                                                        wave.averageRisk
                                                    )}
                                                </strong>
                                            </span>

                                        </div>

                                    </div>


                                    <p>

                                        Components in this
                                        wave can be migrated
                                        at this stage because
                                        the roadmap's
                                        dependency constraints
                                        allow them to proceed.

                                    </p>


                                    <div className="documentation-component-list">

                                        {components.map(
                                            (
                                                component,
                                                componentIndex
                                            ) => (

                                                <div
                                                    className="documentation-component"
                                                    key={
                                                        `${
                                                            component?.className ||
                                                            "component"
                                                        }-${componentIndex}`
                                                    }
                                                >

                                                    <div>

                                                        <strong>
                                                            {
                                                                component?.className ||
                                                                "Unnamed component"
                                                            }
                                                        </strong>

                                                        {component?.packageName && (

                                                            <span>
                                                                {
                                                                    component.packageName
                                                                }
                                                            </span>

                                                        )}

                                                    </div>


                                                    <div className="documentation-metrics">

                                                        <span>
                                                            Impact:{" "}
                                                            <strong>
                                                                {formatScore(
                                                                    component?.impactScore
                                                                )}
                                                            </strong>
                                                        </span>

                                                        <span>
                                                            Difficulty:{" "}
                                                            <strong>
                                                                {formatScore(
                                                                    component?.difficultyScore
                                                                )}
                                                            </strong>
                                                        </span>

                                                        <span>
                                                            Risk:{" "}
                                                            <strong>
                                                                {formatScore(
                                                                    component?.riskScore
                                                                )}
                                                            </strong>
                                                        </span>

                                                    </div>

                                                </div>

                                            )
                                        )}

                                    </div>

                                </section>

                            );

                        }
                    )}

                </div>


                {/* =========================
                    BLOCKED COMPONENTS
                ========================== */}

                {Array.isArray(
                        roadmap.blockedComponents
                    ) &&
                    roadmap.blockedComponents.length > 0 && (

                        <div className="documentation-card blocked-components-card">

                            <h2>
                                Blocked components
                            </h2>

                            <p>
                                These components could not be
                                placed into a migration wave
                                because their dependency
                                constraints could not be
                                resolved.
                            </p>

                            <ul>

                                {roadmap.blockedComponents.map(
                                    (component) => (

                                        <li key={component}>
                                            {component}
                                        </li>

                                    )
                                )}

                            </ul>

                        </div>

                    )}


                {/* =========================
                    DEPENDENCY CYCLES
                ========================== */}

                {Array.isArray(
                        roadmap.dependencyCycles
                    ) &&
                    roadmap.dependencyCycles.length > 0 && (

                        <div className="documentation-card cycle-card">

                            <h2>
                                Dependency cycles
                            </h2>

                            <p>
                                The roadmap detected dependency
                                cycles that prevent a strict
                                dependency-first ordering.
                            </p>

                            <ul>

                                {roadmap.dependencyCycles.map(
                                    (
                                        cycle,
                                        index
                                    ) => (

                                        <li key={index}>

                                            {Array.isArray(cycle)
                                                ? cycle.join(" → ")
                                                : String(cycle)}

                                        </li>

                                    )
                                )}

                            </ul>

                        </div>

                    )}

            </div>

        </div>
    );
}
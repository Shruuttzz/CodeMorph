import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { uploadProject } from "../services/api";

function Upload() {
    const navigate = useNavigate();

    const [file, setFile] = useState(null);
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleUpload = async () => {
        if (!file) {
            alert("Please select a ZIP file.");
            return;
        }

        setLoading(true);
        setResult(null);

        try {
            const response = await uploadProject(file);
            setResult(response.data);
        } catch (error) {
            console.error(error);
            alert("Upload failed. Check the console for details.");
        } finally {
            setLoading(false);
        }
    };

    const parsedCount =
        result?.astAnalysis?.filter(
            (f) => f.status === "parsed"
        ).length || 0;

    const failedCount =
        result?.astAnalysis?.filter(
            (f) => f.status === "failed"
        ).length || 0;

    const deprecatedCount =
        result?.astAnalysis?.filter(
            (f) => f.deprecatedImports?.length > 0
        ).length || 0;

    return (
        <div className="upload-page">

            {/* =========================
                UPLOAD SECTION
            ========================== */}

            <div className="upload-card">

                <h1>Upload Java Project</h1>

                <p className="subtitle">
                    Select a ZIP file containing your Java project.
                </p>

                <label className="file-input-wrapper">

                    <input
                        type="file"
                        accept=".zip"
                        onChange={(e) =>
                            setFile(e.target.files[0])
                        }
                    />

                    <span>
                        {file
                            ? file.name
                            : "Choose a .zip file"}
                    </span>

                </label>

                <button
                    onClick={handleUpload}
                    disabled={loading}
                    className="upload-btn"
                >
                    {loading
                        ? "Analyzing..."
                        : "Upload & Analyze"}
                </button>

            </div>


            {/* =========================
                RESULTS SECTION
            ========================== */}

            {result && (
                <div className="results-section">

                    {/* =========================
                        BASIC SUMMARY
                    ========================== */}

                    <div className="summary-cards">

                        <div className="stat-card">

                            <span className="stat-number">
                                {result.javaFileCount}
                            </span>

                            <span className="stat-label">
                                Java Files
                            </span>

                        </div>


                        <div className="stat-card">

                            <span className="stat-number">
                                {parsedCount}
                            </span>

                            <span className="stat-label">
                                Parsed OK
                            </span>

                        </div>


                        <div className="stat-card stat-warning">

                            <span className="stat-number">
                                {failedCount}
                            </span>

                            <span className="stat-label">
                                Failed to Parse
                            </span>

                        </div>


                        <div className="stat-card stat-danger">

                            <span className="stat-number">
                                {deprecatedCount}
                            </span>

                            <span className="stat-label">
                                Files with Deprecated APIs
                            </span>

                        </div>

                    </div>


                    {/* =========================
                        PROJECT NAME
                    ========================== */}

                    <h2 className="results-title">
                        {result.projectName}
                    </h2>


                    {/* =========================
                        AST ANALYSIS
                    ========================== */}

                    <h2 className="results-title">
                        AST Analysis
                    </h2>

                    <table className="ast-table">

                        <thead>

                        <tr>
                            <th>File</th>
                            <th>Status</th>
                            <th>Classes</th>
                            <th>Methods</th>
                            <th>Deprecated Imports</th>
                        </tr>

                        </thead>


                        <tbody>

                        {result.astAnalysis?.map(
                            (f, i) => (

                                <tr
                                    key={i}
                                    className={
                                        f.status === "failed"
                                            ? "row-failed"
                                            : ""
                                    }
                                >

                                    <td className="file-cell">
                                        {f.file}
                                    </td>


                                    <td>

                                            <span
                                                className={`badge ${
                                                    f.status === "parsed"
                                                        ? "badge-success"
                                                        : "badge-error"
                                                }`}
                                            >
                                                {f.status}
                                            </span>

                                    </td>


                                    <td>
                                        {f.classes?.join(", ") ||
                                            "—"}
                                    </td>


                                    <td className="methods-cell">
                                        {f.methods?.join(", ") ||
                                            "—"}
                                    </td>


                                    <td>

                                        {f.deprecatedImports
                                            ?.length > 0 ? (

                                            <span className="deprecated-tag">
                                                    {f.deprecatedImports.join(
                                                        ", "
                                                    )}
                                                </span>

                                        ) : (
                                            "—"
                                        )}

                                    </td>

                                </tr>

                            )
                        )}

                        </tbody>

                    </table>


                    {/* =========================
                        DEPENDENCY GRAPH BUTTON
                    ========================== */}

                    <button
                        className="upload-btn"
                        onClick={() =>
                            navigate(
                                "/dependency-graph",
                                {
                                    state: { result }
                                }
                            )
                        }
                        style={{
                            marginTop: "40px"
                        }}
                    >
                        View Dependency Graph →
                    </button>


                    {/* =========================
                        MIGRATION IMPACT ANALYSIS
                    ========================== */}

                    {result.migrationSummary && (
                        <div
                            className="migration-section"
                            style={{
                                marginTop: "50px"
                            }}
                        >

                            <h2 className="results-title">
                                Migration Impact Analysis
                            </h2>


                            {/* =========================
                                MIGRATION SUMMARY CARDS
                            ========================== */}

                            <div className="summary-cards">

                                <div className="stat-card">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .totalComponents
                                        }
                                    </span>

                                    <span className="stat-label">
                                        Components
                                    </span>

                                </div>


                                <div className="stat-card stat-danger">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .highRiskComponents
                                        }
                                    </span>

                                    <span className="stat-label">
                                        High Risk
                                    </span>

                                </div>


                                <div className="stat-card stat-warning">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .mediumRiskComponents
                                        }
                                    </span>

                                    <span className="stat-label">
                                        Medium Risk
                                    </span>

                                </div>


                                <div className="stat-card">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .lowRiskComponents
                                        }
                                    </span>

                                    <span className="stat-label">
                                        Low Risk
                                    </span>

                                </div>


                                <div className="stat-card">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .averageRisk
                                        }
                                    </span>

                                    <span className="stat-label">
                                        Average Risk
                                    </span>

                                </div>


                                <div className="stat-card stat-danger">

                                    <span className="stat-number">
                                        {
                                            result
                                                .migrationSummary
                                                .highestRisk
                                        }
                                    </span>

                                    <span className="stat-label">
                                        Highest Risk
                                    </span>

                                </div>

                            </div>


                            {/* =========================
                                COMPONENT RISK TABLE
                            ========================== */}

                            <h3
                                style={{
                                    marginTop: "40px",
                                    marginBottom: "20px"
                                }}
                            >
                                Component-wise Migration Risk
                            </h3>


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

                                        <th>
                                            Blast Radius
                                        </th>

                                        <th>
                                            Centrality
                                        </th>

                                        <th>
                                            Difficulty
                                        </th>

                                        <th>
                                            Impact
                                        </th>

                                        <th>
                                            Risk
                                        </th>

                                    </tr>

                                    </thead>


                                    <tbody>

                                    {result.componentAnalyses
                                        ?.map(
                                            (
                                                component,
                                                index
                                            ) => (

                                                <tr
                                                    key={index}
                                                >

                                                    <td>
                                                        <strong>
                                                            {
                                                                component.className
                                                            }
                                                        </strong>
                                                    </td>


                                                    <td>
                                                        {
                                                            component.loc
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.complexityScore !==
                                                            undefined
                                                                ? component.complexityScore.toFixed(
                                                                    2
                                                                )
                                                                : "—"
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.fanIn
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.fanOut
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.blastRadius
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.centrality !==
                                                            undefined
                                                                ? component.centrality.toFixed(
                                                                    4
                                                                )
                                                                : "—"
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.difficultyScore !==
                                                            undefined
                                                                ? component.difficultyScore.toFixed(
                                                                    2
                                                                )
                                                                : "—"
                                                        }
                                                    </td>


                                                    <td>
                                                        {
                                                            component.impactScore !==
                                                            undefined
                                                                ? component.impactScore.toFixed(
                                                                    2
                                                                )
                                                                : "—"
                                                        }
                                                    </td>


                                                    <td>

                                                            <span
                                                                className={
                                                                    `badge ${
                                                                        component.riskScore >=
                                                                        67
                                                                            ? "badge-error"
                                                                            : component.riskScore >=
                                                                            34
                                                                                ? "badge-warning"
                                                                                : "badge-success"
                                                                    }`
                                                                }
                                                            >

                                                                {
                                                                    component.riskScore !==
                                                                    undefined
                                                                        ? component.riskScore.toFixed(
                                                                            2
                                                                        )
                                                                        : "—"
                                                                }

                                                            </span>

                                                    </td>

                                                </tr>

                                            )
                                        )}

                                    </tbody>

                                </table>

                            </div>

                        </div>
                    )}

                </div>
            )}

        </div>
    );
}

export default Upload;
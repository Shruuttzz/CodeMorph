import { useNavigate } from "react-router-dom";
import { useProject } from "../context/ProjectContext";

function Analysis() {
    const navigate = useNavigate();
    const { projectResult } = useProject();

    const result = projectResult;

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

            <div className="results-section">

                {/* =========================
                    BACK TO UPLOAD
                ========================== */}
                <button
                    type="button"
                    className="upload-btn"
                    onClick={() => navigate("/upload")}
                    style={{
                        marginBottom: "30px"
                    }}
                >
                    ← Back to Upload
                </button>
                {/* =========================
                    PROJECT NAME
                ========================== */}

                <h1 className="results-title">
                    {result?.projectName || "No Project Analysed"}
                </h1>

                <p className="subtitle">
                    {result
                        ? "Project Analysis"
                        : "Please upload and analyse a Java project first."}
                </p>


                {/* =========================
                    SHOW ANALYSIS ONLY IF
                    PROJECT EXISTS
                ========================== */}

                {result && (
                    <>

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

                            {result.astAnalysis?.map((f, i) => (

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
                                        {f.classes?.join(", ") || "—"}
                                    </td>

                                    <td className="methods-cell">
                                        {f.methods?.join(", ") || "—"}
                                    </td>

                                    <td>
                                        {f.deprecatedImports?.length > 0 ? (
                                            <span className="deprecated-tag">
                                                    {f.deprecatedImports.join(", ")}
                                                </span>
                                        ) : (
                                            "—"
                                        )}
                                    </td>

                                </tr>

                            ))}

                            </tbody>

                        </table>


                        {/* =========================
                            DEPENDENCY GRAPH
                        ========================== */}

                        <button
                            type="button"
                            className="upload-btn"
                            onClick={() =>
                                navigate("/dependency-graph")
                            }
                            style={{
                                marginTop: "40px"
                            }}
                        >
                            View Dependency Graph →
                        </button>


                        {/* =========================
                            MIGRATION ANALYSIS
                        ========================== */}

                        <button
                            type="button"
                            className="upload-btn"
                            onClick={() =>
                                navigate("/migration-analysis")
                            }
                            style={{
                                marginTop: "20px"
                            }}
                        >
                            View Migration Analysis and roadmap →
                        </button>

                    </>
                )}

            </div>

        </div>
    );
}

export default Analysis;
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

    const parsedCount = result?.astAnalysis?.filter(f => f.status === "parsed").length || 0;
    const failedCount = result?.astAnalysis?.filter(f => f.status === "failed").length || 0;
    const deprecatedCount = result?.astAnalysis?.filter(f => f.deprecatedImports?.length > 0).length || 0;

    return (
        <div className="upload-page">

            <div className="upload-card">
                <h1>Upload Java Project</h1>
                <p className="subtitle">Select a ZIP file containing your Java project.</p>

                <label className="file-input-wrapper">
                    <input
                        type="file"
                        accept=".zip"
                        onChange={(e) => setFile(e.target.files[0])}
                    />
                    <span>{file ? file.name : "Choose a .zip file"}</span>
                </label>

                <button onClick={handleUpload} disabled={loading} className="upload-btn">
                    {loading ? "Analyzing..." : "Upload & Analyze"}
                </button>
            </div>

            {result && (
                <div className="results-section">

                    <div className="summary-cards">
                        <div className="stat-card">
                            <span className="stat-number">{result.javaFileCount}</span>
                            <span className="stat-label">Java Files</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-number">{parsedCount}</span>
                            <span className="stat-label">Parsed OK</span>
                        </div>
                        <div className="stat-card stat-warning">
                            <span className="stat-number">{failedCount}</span>
                            <span className="stat-label">Failed to Parse</span>
                        </div>
                        <div className="stat-card stat-danger">
                            <span className="stat-number">{deprecatedCount}</span>
                            <span className="stat-label">Files with Deprecated APIs</span>
                        </div>
                    </div>

                    <h2 className="results-title">{result.projectName}</h2>

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
                        {result.astAnalysis.map((f, i) => (
                            <tr key={i} className={f.status === "failed" ? "row-failed" : ""}>
                                <td className="file-cell">{f.file}</td>
                                <td>
                                        <span className={`badge ${f.status === "parsed" ? "badge-success" : "badge-error"}`}>
                                            {f.status}
                                        </span>
                                </td>
                                <td>{f.classes?.join(", ") || "—"}</td>
                                <td className="methods-cell">{f.methods?.join(", ") || "—"}</td>
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

                    <button
                        className="upload-btn"
                        onClick={() =>
                            navigate("/dependency-graph", {
                                state: { result }
                            })
                        }
                        style={{ marginTop: "40px" }}
                    >
                        View Dependency Graph →
                    </button>
                </div>
            )}

        </div>
    );
}

export default Upload;
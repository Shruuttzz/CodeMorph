import { useNavigate } from "react-router-dom";
import DependencyGraph from "../components/DependencyGraph";
import { useProject } from "../context/ProjectContext";

function DependencyGraphPage() {
    const navigate = useNavigate();

    const { projectResult } = useProject();

    const result = projectResult;

    if (!result || !result.dependencyGraph) {
        return (
            <div className="upload-page">
                <div className="upload-card">

                    <h1>No Dependency Graph Available</h1>

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

                {/* Back to Analysis */}
                <button
                    className="upload-btn"
                    onClick={() => navigate("/analysis")}
                    style={{ marginBottom: "20px" }}
                >
                    ← Back to Analysis
                </button>


                <h1 className="results-title">
                    Dependency Graph
                </h1>


                <p className="tree-hint">
                    Each box is a class. Arrows show which class depends on
                    (uses) which other class.
                </p>


                <DependencyGraph
                    graphData={result.dependencyGraph}
                />

            </div>

        </div>
    );
}

export default DependencyGraphPage;
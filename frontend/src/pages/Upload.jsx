import { useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { uploadProject } from "../services/api";
import { useProject } from "../context/ProjectContext";

function Upload() {
    const navigate = useNavigate();
    const { setProjectResult } = useProject();

    const fileInputRef = useRef(null);

    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [dragActive, setDragActive] = useState(false);


    // =========================
    // FILE SELECTION
    // =========================

    const handleFileSelect = (selectedFile) => {
        if (!selectedFile) {
            return;
        }

        if (!selectedFile.name.toLowerCase().endsWith(".zip")) {
            alert("Please select a ZIP file.");
            return;
        }

        setFile(selectedFile);
    };


    // =========================
    // FILE EXPLORER
    // =========================

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];

        handleFileSelect(selectedFile);
    };


    // =========================
    // DRAG & DROP
    // =========================

    const handleDragOver = (event) => {
        event.preventDefault();
        setDragActive(true);
    };


    const handleDragLeave = (event) => {
        event.preventDefault();
        setDragActive(false);
    };


    const handleDrop = (event) => {
        event.preventDefault();
        setDragActive(false);

        const droppedFile = event.dataTransfer.files[0];

        handleFileSelect(droppedFile);
    };


    // =========================
    // UPLOAD BOX CLICK
    // =========================

    const handleBoxClick = () => {
        fileInputRef.current?.click();
    };


    // =========================
    // ANALYSE
    // =========================

    const handleAnalyse = async () => {
        if (!file) {
            return;
        }

        setLoading(true);

        try {
            const response = await uploadProject(file);

            // Save result in ProjectContext
            setProjectResult(response.data);

            // Go to Analysis page
            navigate("/analysis");

        } catch (error) {
            console.error("Upload failed:", error);

            alert(
                "Upload failed. Check the console for details."
            );

        } finally {
            setLoading(false);
        }
    };


    // =========================
    // MAIN BUTTON
    // =========================

    const handleMainButtonClick = () => {

        // No file selected
        // → open file explorer
        if (!file) {
            fileInputRef.current?.click();
            return;
        }

        // File selected
        // → analyse project
        handleAnalyse();
    };


    return (
        <div className="upload-page">

            <div className="upload-card">

                <h1>
                    Upload Java Project
                </h1>

                <p className="subtitle">
                    Upload a ZIP file containing your Java project.
                </p>


                {/* =========================
                    HIDDEN FILE INPUT
                ========================== */}

                <input
                    ref={fileInputRef}
                    type="file"
                    accept=".zip"
                    onChange={handleFileChange}
                    hidden
                />


                {/* =========================
                    SINGLE UPLOAD BOX
                ========================== */}

                <div
                    className={`upload-drop-zone ${
                        dragActive ? "drag-active" : ""
                    }`}
                    onClick={handleBoxClick}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                >



                    {file ? (
                        <>
                            <h2 className="upload-file-name">
                                {file.name}
                            </h2>

                            <p className="upload-file-status">
                                ZIP file selected
                            </p>
                        </>
                    ) : (
                        <>
                            <h2>
                                Choose ZIP File
                            </h2>


                        </>
                    )}

                </div>


                {/* =========================
                    SINGLE BUTTON BELOW BOX
                ========================== */}

                <button
                    type="button"
                    className="upload-btn"
                    onClick={handleMainButtonClick}
                    disabled={loading}
                >
                    {loading
                        ? "ANALYSING..."
                        : file
                            ? "ANALYSE"
                            : "UPLOAD"}
                </button>

            </div>

        </div>
    );
}

export default Upload;
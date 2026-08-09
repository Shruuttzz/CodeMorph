import { useState } from "react";

function Upload() {

    const [file, setFile] = useState(null);

    const handleUpload = () => {

        if (!file) {

            alert("Please select a ZIP file.");

            return;

        }

        console.log("Selected File:");

        console.log(file);

        alert("File selected successfully!");

        // Later:
        // uploadProject(file);

    };

    return (

        <div className="upload-container">

            <h1>Upload Java Project</h1>

            <p>Select a ZIP file containing your Java project.</p>

            <input
                type="file"
                accept=".zip"
                onChange={(e) => setFile(e.target.files[0])}
            />

            <br /><br />

            <button onClick={handleUpload}>
                Upload
            </button>

        </div>

    );

}

export default Upload;
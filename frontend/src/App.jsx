import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Upload from "./pages/Upload";
import DependencyGraphPage from "./pages/DependencyGraphPage";
import MigrationOverview from "./pages/MigrationOverview";

import { ProjectProvider } from "./context/ProjectContext";
import Analysis from "./pages/analysis";

function App() {
    return (
        <ProjectProvider>

            <BrowserRouter>

                <Routes>

                    <Route
                        path="/"
                        element={<Login />}
                    />

                    <Route
                        path="/upload"
                        element={<Upload />}
                    />

                    <Route
                        path="/dependency-graph"
                        element={<DependencyGraphPage />}
                    />

                    <Route
                        path="/migration-analysis"
                        element={<MigrationOverview />}
                    />

                    <Route
                        path="/analysis"
                        element={<Analysis />}
                    />


                </Routes>

            </BrowserRouter>

        </ProjectProvider>
    );
}

export default App;
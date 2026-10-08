import { BrowserRouter, Routes, Route } from "react-router-dom";
import "./App.css"
import Login from "./pages/Login";
import Upload from "./pages/Upload";
import DependencyGraphPage from "./pages/DependencyGraphPage";
import MigrationOverview from "./pages/MigrationOverview";
import MigrationRoadmap from "./pages/MigrationRoadmap";
import MigrationRoadmapDocumentation from "./pages/MigrationRoadmapDocumentation";
import Analysis from "./pages/analysis";

import { ProjectProvider } from "./context/ProjectContext";

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
                        path="/migration-roadmap"
                        element={<MigrationRoadmap />}
                    />

                    <Route
                        path="/migration-roadmap/documentation"
                        element={
                            <MigrationRoadmapDocumentation />
                        }
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
import { createContext, useContext, useState } from "react";

const ProjectContext = createContext(null);

export function ProjectProvider({ children }) {
    const [projectResult, setProjectResult] = useState(null);

    return (
        <ProjectContext.Provider
            value={{
                projectResult,
                setProjectResult
            }}
        >
            {children}
        </ProjectContext.Provider>
    );
}

export function useProject() {
    return useContext(ProjectContext);
}
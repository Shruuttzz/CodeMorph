import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Upload from "./pages/Upload";
import DependencyGraphPage from "./pages/DependencyGraphPage";

function App() {

  return (
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
        </Routes>
      </BrowserRouter>
  );
}

export default App;
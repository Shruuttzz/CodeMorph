import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Upload from "./pages/Upload";

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

        </Routes>

      </BrowserRouter>

  );

}

export default App;
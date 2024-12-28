import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import AppLayout from "./components/layout/AppLayout";
import Dashboard from "./pages/Dashboard";
import Users from "./pages/Users";
import Exercises from "./pages/Exercises";

const App = () => {
  return (
    <Router>
      <AppLayout>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/users" element={<Users />} />
          <Route path="/exercises" element={<Exercises />} />
          <Route path="*" element={<Dashboard />} />
        </Routes>
      </AppLayout>
    </Router>
  );
};

export default App;

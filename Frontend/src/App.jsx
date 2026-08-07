import { Routes, Route } from "react-router-dom";

import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";

import ProtectedRoute from "./components/ProtectedRoute";
import AppLayout from "./layouts/AppLayout";
import MyRequestsPage from "./pages/MyRequestsPage";
import RequestDetailsPage from "./pages/RequestDetailsPage";
import CreateRequestPage from "./pages/CreateRequestPage";
import EditRequestPage from "./pages/EditRequestPage";

function App() {
  return (
    <Routes>

      <Route path="/" element={<LoginPage />} />

      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route
          path="/dashboard"
          element={<DashboardPage />}
          
        />
        
        <Route
          path="/my-requests"
          element={<MyRequestsPage />}
        />
        <Route
          path="/change-requests/new"
          element={<CreateRequestPage />}
        />
        <Route
          path="/change-requests/:id/edit"
          element={<EditRequestPage />}
        />
        <Route
          path="/change-requests/:id"
          element={<RequestDetailsPage />}
        />
        
      </Route>
      
    </Routes>
  );
}

export default App;
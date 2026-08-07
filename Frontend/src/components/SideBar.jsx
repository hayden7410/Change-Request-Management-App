import {
  LayoutDashboard,
  FileText,
  PlusCircle,
  ClipboardList,
  Code2,
  LogOut,
} from "lucide-react";

import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Sidebar.css";

function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const permissions = user?.permissions || [];

  const hasPermission = (permission) =>
    permissions.includes(permission);

  function handleLogout() {
    logout();
    navigate("/");
  }

  return (
    <aside className="sidebar">

      <div className="sidebar-brand">
        <div className="brand-icon">CR</div>

        <div>
          <h2>ChangeFlow</h2>
          <p>Change Management</p>
        </div>
      </div>

      <nav className="sidebar-nav">

        <NavLink to="/dashboard" className="sidebar-link">
          <LayoutDashboard size={20} />
          <span>Dashboard</span>
        </NavLink>

        {hasPermission("VIEW_SUBMITTED_REQUESTS") && (
          <NavLink to="/my-requests" className="sidebar-link">
            <FileText size={20} />
            <span>My Requests</span>
          </NavLink>
        )}

        {hasPermission("CREATE_CHANGE_REQUEST") && (
          <NavLink to="/change-requests/new" className="sidebar-link">
            <PlusCircle size={20} />
            <span>New Request</span>
          </NavLink>
        )}

        {hasPermission("VIEW_ALL_REQUESTS") && (
          <NavLink to="/review" className="sidebar-link">
            <ClipboardList size={20} />
            <span>Review Queue</span>
          </NavLink>
        )}

        {hasPermission("VIEW_ASSIGNED_REQUESTS") && (
          <NavLink to="/assigned-to-me" className="sidebar-link">
            <Code2 size={20} />
            <span>Assigned Requests</span>
          </NavLink>
        )}

      </nav>

      <div className="sidebar-footer">

        <div className="sidebar-user">
          <div className="user-avatar">
            {user?.firstName?.charAt(0)}
            {user?.lastName?.charAt(0)}
          </div>

          <div>
            <strong>
              {user?.firstName} {user?.lastName}
            </strong>

            <span>{user?.email}</span>
          </div>
        </div>

        <button
          className="logout-button"
          onClick={handleLogout}
        >
          <LogOut size={18} />
          Logout
        </button>

      </div>

    </aside>
  );
}

export default Sidebar;
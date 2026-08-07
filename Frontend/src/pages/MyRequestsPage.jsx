import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getMyRequests } from "../services/changeRequestService";
import StatusBadge from "../components/StatusBadge";

import "./MyRequestsPage.css";

function MyRequestsPage() {
  const navigate = useNavigate();

  const [requests, setRequests] = useState([]);
  const [statusFilter, setStatusFilter] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadRequests() {
      setLoading(true);
      setError("");

      try {
        const data = await getMyRequests(
          statusFilter || null
        );

        setRequests(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadRequests();
  }, [statusFilter]);

  return (
    <div className="my-requests-page">

      <div className="page-header">
        <div>
          <h1>My Requests</h1>
          <p>
            View and track change requests you have created.
          </p>
        </div>

        <button
          className="new-request-button"
          onClick={() =>
            navigate("/change-requests/new")
          }
        >
          + New Request
        </button>
      </div>

      <div className="request-toolbar">

        <select
          value={statusFilter}
          onChange={(event) =>
            setStatusFilter(event.target.value)
          }
        >
          <option value="">All Statuses</option>
          <option value="DRAFT">Draft</option>
          <option value="SUBMITTED">Submitted</option>
          <option value="UNDER_REVIEW">
            Under Review
          </option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
          <option value="IMPLEMENTATION_PENDING">
            Implementation Pending
          </option>
          <option value="IMPLEMENTED">
            Implemented
          </option>
          <option value="CLOSED">Closed</option>
        </select>

      </div>

      {loading && <p>Loading requests...</p>}

      {error && (
        <p className="error-message">
          {error}
        </p>
      )}

      {!loading && !error && (
        <div className="request-table-container">

          <table className="request-table">

            <thead>
              <tr>
                <th>ID</th>
                <th>Request</th>
                <th>Department</th>
                <th>Status</th>
                <th>Urgency</th>
                <th>Priority</th>
                <th>Created</th>
              </tr>
            </thead>

            <tbody>

              {requests.map((request) => (
                <tr
                  key={request.id}
                  onClick={() =>
                    navigate(
                      `/change-requests/${request.id}`
                    )
                  }
                >
                  <td>
                    CR-{String(request.id).padStart(4, "0")}
                  </td>

                  <td>
                    <strong>{request.title}</strong>
                  </td>

                  <td>
                    {request.assignedDepartmentName}
                  </td>

                  <td>
                    <StatusBadge
                      status={request.status}
                    />
                  </td>

                  <td>{request.urgency}</td>

                  <td>{request.priority}</td>

                  <td>
                    {new Date(
                      request.createdAt
                    ).toLocaleDateString()}
                  </td>
                </tr>
              ))}

            </tbody>

          </table>

          {requests.length === 0 && (
            <div className="empty-state">
              No change requests found.
            </div>
          )}

        </div>
      )}

    </div>
  );
}

export default MyRequestsPage;
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { getReviewQueue } from "../services/changeRequestService";
import StatusBadge from "../components/StatusBadge";

import "./ReviewQueuePage.css";

function ReviewQueuePage() {
  const navigate = useNavigate();

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadReviewQueue() {
      try {
        setLoading(true);
        setError("");

        const data = await getReviewQueue();

        setRequests(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadReviewQueue();
  }, []);

  if (loading) {
    return <p>Loading review queue...</p>;
  }

  if (error) {
    return (
      <p className="error-message">
        {error}
      </p>
    );
  }

  return (
    <div className="review-queue-page">

      <div className="page-header">
        <div>
          <h1>Review Queue</h1>

          <p>
            Review and manage submitted change requests.
          </p>
        </div>
      </div>

      <div className="review-table-container">

        <table className="review-table">

          <thead>
            <tr>
              <th>ID</th>
              <th>Request</th>
              <th>Department</th>
              <th>Status</th>
              <th>Urgency</th>
              <th>Priority</th>
              <th>Requester</th>
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
                  <strong>
                    {request.title}
                  </strong>
                </td>

                <td>
                  {request.assignedDepartmentName}
                </td>

                <td>
                  <StatusBadge
                    status={request.status}
                  />
                </td>

                <td>
                  {request.urgency}
                </td>

                <td>
                  {request.priority}
                </td>

                <td>
                  {request.submittedByEmail}
                </td>

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
            No requests are currently available for review.
          </div>
        )}

      </div>

    </div>
  );
}

export default ReviewQueuePage;
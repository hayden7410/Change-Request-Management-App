import { useState } from "react";

import {
  updatePriority,
  updateStatus,
} from "../services/changeRequestService";

import "./ReviewerControls.css";

function ReviewerControls({
  request,
  user,
  onRequestUpdated,
}) {
  const [priority, setPriority] = useState(request.priority);
  const [status, setStatus] = useState(request.status);

  const [savingPriority, setSavingPriority] = useState(false);
  const [savingStatus, setSavingStatus] = useState(false);

  const [error, setError] = useState("");

  const permissions = user?.permissions || [];

  const canUpdatePriority =
    permissions.includes("UPDATE_REQUEST_PRIORITY");

  const canUpdateStatus =
    permissions.includes("UPDATE_REQUEST_STATUS");

  async function handlePriorityUpdate() {
    try {
      setSavingPriority(true);
      setError("");

      const updatedRequest = await updatePriority(
        request.id,
        priority
      );

      onRequestUpdated(updatedRequest);
    } catch (error) {
      setError(error.message);
    } finally {
      setSavingPriority(false);
    }
  }

  async function handleStatusUpdate() {
    try {
      setSavingStatus(true);
      setError("");

      const updatedRequest = await updateStatus(
        request.id,
        status
      );

      onRequestUpdated(updatedRequest);
    } catch (error) {
      setError(error.message);

      // Put dropdown back to the real current status
      setStatus(request.status);
    } finally {
      setSavingStatus(false);
    }
  }

  if (!canUpdatePriority && !canUpdateStatus) {
    return null;
  }

  return (
    <section className="reviewer-controls">

      <h2>Review Actions</h2>

      <div className="reviewer-controls-grid">

        {canUpdatePriority && (
          <div className="review-control">

            <label>Priority</label>

            <div className="review-control-row">

              <select
                value={priority}
                onChange={(event) =>
                  setPriority(event.target.value)
                }
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>

              <button
                onClick={handlePriorityUpdate}
                disabled={
                  savingPriority ||
                  priority === request.priority
                }
              >
                {savingPriority
                  ? "Saving..."
                  : "Update Priority"}
              </button>

            </div>

          </div>
        )}

        {canUpdateStatus && (
          <div className="review-control">

            <label>Status</label>

            <div className="review-control-row">

              <select
                value={status}
                onChange={(event) =>
                  setStatus(event.target.value)
                }
              >
                <option value={request.status}>
                  {request.status.replaceAll("_", " ")}
                </option>

                {request.status === "SUBMITTED" && (
                  <option value="UNDER_REVIEW">
                    Under Review
                  </option>
                )}

                {request.status === "UNDER_REVIEW" && (
                  <>
                    <option value="APPROVED">
                      Approved
                    </option>

                    <option value="REJECTED">
                      Rejected
                    </option>
                  </>
                )}

                {request.status === "APPROVED" && (
                  <option value="IMPLEMENTATION_PENDING">
                    Implementation Pending
                  </option>
                )}

                {request.status ===
                  "IMPLEMENTATION_PENDING" && (
                  <option value="IMPLEMENTED">
                    Implemented
                  </option>
                )}

                {request.status === "IMPLEMENTED" && (
                  <option value="CLOSED">
                    Closed
                  </option>
                )}

              </select>

              <button
                onClick={handleStatusUpdate}
                disabled={
                  savingStatus ||
                  status === request.status
                }
              >
                {savingStatus
                  ? "Updating..."
                  : "Update Status"}
              </button>

            </div>

          </div>
        )}

      </div>

      {error && (
        <p className="error-message">
          {error}
        </p>
      )}

    </section>
  );
}

export default ReviewerControls;
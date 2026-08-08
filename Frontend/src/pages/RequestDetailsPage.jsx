import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import {
  getChangeRequestById,
  getStatusHistory,
  getComments,
  addComment,
} from "../services/changeRequestService";

import StatusBadge from "../components/StatusBadge";
import WorkflowStepper from "../components/WorkflowStepper";
import ReviewerControls from "../components/ReviewerControls";

import "./RequestDetailsPage.css";

function RequestDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [request, setRequest] = useState(null);
  const [history, setHistory] = useState([]);
  const [comments, setComments] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [newComment, setNewComment] = useState("");
  const [submittingComment, setSubmittingComment] =
    useState(false);
  const [commentError, setCommentError] = useState("");

  // Load request details, history, and comments
  useEffect(() => {
    async function loadRequest() {
      try {
        setLoading(true);
        setError("");

        const [
          requestData,
          historyData,
          commentData,
        ] = await Promise.all([
          getChangeRequestById(id),
          getStatusHistory(id),
          getComments(id),
        ]);

        setRequest(requestData);
        setHistory(historyData);
        setComments(commentData);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadRequest();
  }, [id]);

  // Add new comment
  async function handleAddComment(event) {
    event.preventDefault();

    if (!newComment.trim()) {
      return;
    }

    try {
      setSubmittingComment(true);
      setCommentError("");

      const createdComment = await addComment(
        id,
        newComment.trim()
      );

      setComments((currentComments) => [
        ...currentComments,
        createdComment,
      ]);

      setNewComment("");
    } catch (error) {
      setCommentError(error.message);
    } finally {
      setSubmittingComment(false);
    }
  }

  // Loading state
  if (loading) {
    return <p>Loading request...</p>;
  }

  // Error state
  if (error) {
    return (
      <p className="error-message">
        {error}
      </p>
    );
  }

  // Safety check
  if (!request) {
    return null;
  }

  // Only the owner can edit their own draft
  const canEditDraft =
    request.status === "DRAFT" &&
    request.submittedByEmail === user?.email;

  // Draft requests cannot have comments added
  const canComment =
    request.status !== "DRAFT" &&
    user?.permissions?.includes("COMMENT_ON_REQUEST");

  return (
    <div className="request-details-page">

      {/* Page Header */}
      <div className="details-header">

        <div>
          <span className="request-number">
            CR-{String(request.id).padStart(4, "0")}
          </span>

          <h1>{request.title}</h1>
        </div>

        <div className="details-header-actions">

          <StatusBadge status={request.status} />

          {canEditDraft && (
            <button
              className="primary-button"
              onClick={() =>
                navigate(
                  `/change-requests/${request.id}/edit`
                )
              }
            >
              Edit Draft
            </button>
          )}

        </div>

      </div>

      {/* Workflow Progress */}
      {request.status !== "DRAFT" && (
        <div className="workflow-card">

          <h2>Change Progress</h2>

          <WorkflowStepper
            status={request.status}
          />

        </div>
      )}
      <ReviewerControls
        request={request}
        user={user}
        onRequestUpdated={(updatedRequest) =>
          setRequest(updatedRequest)
        }
      />

      {/* Main Request Information */}
      <div className="details-grid">

        <section className="details-card">

          <h2>Request Details</h2>

          <div className="detail-item">

            <span>Description</span>

            <p>{request.description}</p>

          </div>

          <div className="detail-item">

            <span>Business Justification</span>

            <p>
              {request.businessJustification ||
                "No business justification provided."}
            </p>

          </div>

          <div className="detail-row">

            <div>
              <span>Department</span>

              <strong>
                {request.assignedDepartmentName}
              </strong>
            </div>

            <div>
              <span>Urgency</span>

              <strong>
                {request.urgency}
              </strong>
            </div>

            <div>
              <span>Priority</span>

              <strong>
                {request.priority}
              </strong>
            </div>

          </div>

        </section>

        {/* Assignment Information */}
        <section className="details-card">

          <h2>Assignment</h2>

          <div className="detail-item">

            <span>Submitted By</span>

            <strong>
              {request.submittedByEmail}
            </strong>

          </div>

          <div className="detail-item">

            <span>Assigned Developer</span>

            <strong>
              {request.assignedDeveloperEmail ||
                "Not assigned"}
            </strong>

          </div>

          {request.assignedByEmail && (
            <div className="detail-item">

              <span>Assigned By</span>

              <strong>
                {request.assignedByEmail}
              </strong>

            </div>
          )}

          {request.assignedAt && (
            <div className="detail-item">

              <span>Assigned At</span>

              <strong>
                {new Date(
                  request.assignedAt
                ).toLocaleString()}
              </strong>

            </div>
          )}

          <div className="detail-item">

            <span>Created</span>

            <strong>
              {new Date(
                request.createdAt
              ).toLocaleString()}
            </strong>

          </div>

        </section>

      </div>

      {/* Status History */}
      <section className="details-card">

        <h2>Status History</h2>

        {history.length === 0 ? (
          <p>No status changes recorded yet.</p>
        ) : (
          <div className="history-list">

            {history.map((entry) => (
              <div
                className="history-item"
                key={entry.id}
              >

                <strong>
                  {entry.previousStatus}
                  {" → "}
                  {entry.newStatus}
                </strong>

                <span>
                  Changed by {entry.changedByEmail}
                </span>

                <span>
                  {new Date(
                    entry.changedAt
                  ).toLocaleString()}
                </span>

              </div>
            ))}

          </div>
        )}

      </section>

      {/* Comments */}
      <section className="details-card">

        <h2>Comments</h2>

        {comments.length === 0 ? (
          <p>No comments yet.</p>
        ) : (
          <div className="comments-list">

            {comments.map((comment) => (
              <div
                className="comment-item"
                key={comment.id}
              >

                <strong>
                  {comment.authorEmail}
                </strong>

                <p>
                  {comment.content}
                </p>

                <span>
                  {new Date(
                    comment.createdAt
                  ).toLocaleString()}
                </span>

              </div>
            ))}

          </div>
        )}

        {/* Comment Form */}
        {canComment && (
          <form
            className="comment-form"
            onSubmit={handleAddComment}
          >

            <textarea
              placeholder="Add a comment..."
              value={newComment}
              onChange={(event) =>
                setNewComment(event.target.value)
              }
              maxLength={2000}
              rows={4}
            />

            {commentError && (
              <p className="error-message">
                {commentError}
              </p>
            )}

            <div className="comment-form-footer">

              <span>
                {newComment.length}/2000
              </span>

              <button
                type="submit"
                disabled={
                  submittingComment ||
                  !newComment.trim()
                }
              >
                {submittingComment
                  ? "Posting..."
                  : "Post Comment"}
              </button>

            </div>

          </form>
        )}

        {request.status === "DRAFT" && (
          <p className="comment-disabled-message">
            Comments will be available after the
            request is submitted.
          </p>
        )}

      </section>

    </div>
  );
}

export default RequestDetailsPage;
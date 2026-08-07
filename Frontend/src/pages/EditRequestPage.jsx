import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import {
  getChangeRequestById,
  updateDraft,
} from "../services/changeRequestService";

import { getDepartments } from "../services/departmentService";

import "./CreateRequestPage.css";

function EditRequestPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);

  const [form, setForm] = useState({
    title: "",
    description: "",
    businessJustification: "",
    urgency: "MEDIUM",
    assignedDepartmentId: "",
  });

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const [request, departmentData] = await Promise.all([
          getChangeRequestById(id),
          getDepartments(),
        ]);

        if (request.status !== "DRAFT") {
          setError("Only draft requests can be edited.");
          return;
        }

        setDepartments(departmentData);

        setForm({
          title: request.title,
          description: request.description,
          businessJustification:
            request.businessJustification || "",
          urgency: request.urgency,
          assignedDepartmentId:
            request.assignedDepartmentId,
        });
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [id]);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleUpdate(action) {
    try {
      setSubmitting(true);
      setError("");

      const updatedRequest = await updateDraft(id, {
        title: form.title,
        description: form.description,
        businessJustification:
          form.businessJustification,
        urgency: form.urgency,
        assignedDepartmentId:
          Number(form.assignedDepartmentId),
        action,
      });

      navigate(
        `/change-requests/${updatedRequest.id}`
      );
    } catch (error) {
      setError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) {
    return <p>Loading draft...</p>;
  }

  return (
    <div className="create-request-page">

      <div className="page-header">
        <div>
          <h1>Edit Draft</h1>
          <p>
            Update your change request before submitting it
            for review.
          </p>
        </div>
      </div>

      {error && (
        <p className="error-message">
          {error}
        </p>
      )}

      {!error && (
        <div className="request-form-card">

          <div className="form-group">
            <label>Title</label>

            <input
              name="title"
              value={form.title}
              onChange={handleChange}
              maxLength={200}
            />
          </div>

          <div className="form-group">
            <label>Description</label>

            <textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              rows={5}
            />
          </div>

          <div className="form-group">
            <label>Business Justification</label>

            <textarea
              name="businessJustification"
              value={form.businessJustification}
              onChange={handleChange}
              rows={4}
            />
          </div>

          <div className="form-row">

            <div className="form-group">
              <label>Department</label>

              <select
                name="assignedDepartmentId"
                value={form.assignedDepartmentId}
                onChange={handleChange}
              >
                {departments.map((department) => (
                  <option
                    key={department.id}
                    value={department.id}
                  >
                    {department.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Urgency</label>

              <select
                name="urgency"
                value={form.urgency}
                onChange={handleChange}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">
                  Critical
                </option>
              </select>
            </div>

          </div>

          <div className="form-actions">

            <button
              className="secondary-button"
              disabled={submitting}
              onClick={() =>
                handleUpdate("SAVE_DRAFT")
              }
            >
              Save Changes
            </button>

            <button
              className="primary-button"
              disabled={submitting}
              onClick={() =>
                handleUpdate("SUBMIT")
              }
            >
              {submitting
                ? "Saving..."
                : "Submit Request"}
            </button>

          </div>

        </div>
      )}

    </div>
  );
}

export default EditRequestPage;
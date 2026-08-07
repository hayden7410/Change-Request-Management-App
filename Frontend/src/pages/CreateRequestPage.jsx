import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { createChangeRequest } from "../services/changeRequestService";
import { getDepartments } from "../services/departmentService";

import "./CreateRequestPage.css";

function CreateRequestPage() {
  const navigate = useNavigate();

  const [departments, setDepartments] = useState([]);

  const [form, setForm] = useState({
    title: "",
    description: "",
    businessJustification: "",
    urgency: "MEDIUM",
    assignedDepartmentId: "",
  });

  const [loadingDepartments, setLoadingDepartments] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDepartments() {
      try {
        const data = await getDepartments();
        setDepartments(data);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoadingDepartments(false);
      }
    }

    loadDepartments();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleCreate(action) {
    setSubmitting(true);
    setError("");

    try {
      const createdRequest = await createChangeRequest({
        title: form.title,
        description: form.description,
        businessJustification: form.businessJustification,
        urgency: form.urgency,
        assignedDepartmentId: Number(form.assignedDepartmentId),
        action,
      });

      navigate(`/change-requests/${createdRequest.id}`);

    } catch (error) {
      setError(error.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="create-request-page">

      <div className="page-header">
        <div>
          <h1>Create Change Request</h1>
          <p>
            Provide the information required to evaluate your proposed change.
          </p>
        </div>
      </div>

      <div className="request-form-card">

        <div className="form-group">
          <label>Title</label>

          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            maxLength={200}
            placeholder="Enter a concise request title"
          />
        </div>

        <div className="form-group">
          <label>Description</label>

          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
            rows={5}
            placeholder="Describe the proposed change"
          />
        </div>

        <div className="form-group">
          <label>Business Justification</label>

          <textarea
            name="businessJustification"
            value={form.businessJustification}
            onChange={handleChange}
            rows={4}
            placeholder="Explain why this change is needed"
          />
        </div>

        <div className="form-row">

          <div className="form-group">
            <label>Department</label>

            <select
              name="assignedDepartmentId"
              value={form.assignedDepartmentId}
              onChange={handleChange}
              disabled={loadingDepartments}
            >
              <option value="">
                Select a department
              </option>

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
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

        </div>

        {error && (
          <p className="error-message">
            {error}
          </p>
        )}

        <div className="form-actions">

          <button
            className="secondary-button"
            disabled={submitting}
            onClick={() => handleCreate("SAVE_DRAFT")}
          >
            Save as Draft
          </button>

          <button
            className="primary-button"
            disabled={submitting}
            onClick={() => handleCreate("SUBMIT")}
          >
            {submitting ? "Saving..." : "Submit Request"}
          </button>

        </div>

      </div>

    </div>
  );
}

export default CreateRequestPage;